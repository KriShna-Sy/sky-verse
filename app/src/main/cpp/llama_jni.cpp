// Sky Verse <-> llama.cpp bridge. Pure llama.h C API (pinned to llama.cpp b12212).
#include <jni.h>
#include <android/log.h>
#include <unistd.h>

#include <algorithm>
#include <atomic>
#include <mutex>
#include <string>
#include <vector>

#include "llama.h"

#define TAG "SkyLlama"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

static llama_model   *g_model = nullptr;
static llama_context *g_ctx   = nullptr;
static std::mutex     g_mutex;
static std::atomic<bool> g_stop{false};
static int            g_n_ctx = 2048;

static void log_cb(ggml_log_level level, const char *text, void *) {
    if (level == GGML_LOG_LEVEL_ERROR) __android_log_print(ANDROID_LOG_ERROR, TAG, "%s", text);
    else if (level == GGML_LOG_LEVEL_WARN) __android_log_print(ANDROID_LOG_WARN, TAG, "%s", text);
}

static bool abort_cb(void *) { return g_stop.load(); }

static std::string jstr(JNIEnv *env, jstring s) {
    if (!s) return {};
    const char *c = env->GetStringUTFChars(s, nullptr);
    std::string out(c);
    env->ReleaseStringUTFChars(s, c);
    return out;
}

// Java's NewStringUTF needs *modified* UTF-8; build via byte[] -> String(UTF-8) instead.
static jstring utf8_to_jstring(JNIEnv *env, const std::string &s) {
    jbyteArray bytes = env->NewByteArray((jsize) s.size());
    env->SetByteArrayRegion(bytes, 0, (jsize) s.size(), (const jbyte *) s.data());
    jclass strCls = env->FindClass("java/lang/String");
    jmethodID ctor = env->GetMethodID(strCls, "<init>", "([BLjava/lang/String;)V");
    jstring charset = env->NewStringUTF("UTF-8");
    auto out = (jstring) env->NewObject(strCls, ctor, bytes, charset);
    env->DeleteLocalRef(bytes);
    env->DeleteLocalRef(charset);
    env->DeleteLocalRef(strCls);
    return out;
}

// Length of the longest prefix of `s` that is complete UTF-8 (tokens can split code points).
static size_t valid_utf8_prefix(const std::string &s) {
    size_t i = 0, n = s.size();
    while (i < n) {
        unsigned char c = s[i];
        size_t len = c < 0x80 ? 1 : (c >> 5) == 0x6 ? 2 : (c >> 4) == 0xE ? 3 : (c >> 3) == 0x1E ? 4 : 1;
        if (i + len > n) break;
        i += len;
    }
    return i;
}

static void free_all() {
    if (g_ctx)   { llama_free(g_ctx);         g_ctx = nullptr; }
    if (g_model) { llama_model_free(g_model); g_model = nullptr; }
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_skyverse_app_core_ai_LlamaEngine_loadModelJni(JNIEnv *env, jobject, jstring jpath, jint n_ctx) {
    std::lock_guard<std::mutex> lock(g_mutex);
    static bool backend_ready = false;
    if (!backend_ready) {
        llama_log_set(log_cb, nullptr);
        llama_backend_init();
        backend_ready = true;
    }
    free_all();

    const std::string path = jstr(env, jpath);
    llama_model_params mp = llama_model_default_params();
    mp.n_gpu_layers = 0;
    g_model = llama_model_load_from_file(path.c_str(), mp);
    if (!g_model) { LOGE("model load failed: %s", path.c_str()); return JNI_FALSE; }

    const int cores   = (int) sysconf(_SC_NPROCESSORS_ONLN);
    const int threads = std::max(2, std::min(4, cores - 2));
    g_n_ctx = n_ctx > 0 ? n_ctx : 2048;

    llama_context_params cp = llama_context_default_params();
    cp.n_ctx           = (uint32_t) g_n_ctx;
    cp.n_batch         = 512;
    cp.n_ubatch        = 512;
    cp.n_threads       = threads;
    cp.n_threads_batch = threads;
    g_ctx = llama_init_from_model(g_model, cp);
    if (!g_ctx) { LOGE("context init failed"); free_all(); return JNI_FALSE; }
    llama_set_abort_callback(g_ctx, abort_cb, nullptr);

    LOGI("loaded %s | n_ctx=%d threads=%d", path.c_str(), g_n_ctx, threads);
    return JNI_TRUE;
}

// roles/contents: parallel arrays ("system" | "user" | "assistant").
// callback: object with `boolean onToken(String piece)`; return false to stop.
extern "C" JNIEXPORT jstring JNICALL
Java_com_skyverse_app_core_ai_LlamaEngine_generateJni(JNIEnv *env, jobject,
                                                      jobjectArray jroles, jobjectArray jcontents,
                                                      jint max_tokens, jfloat temperature,
                                                      jobject callback) {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (!g_model || !g_ctx) return utf8_to_jstring(env, "");
    g_stop = false;

    // ---- build chat prompt from the model's own template ----
    const jsize n_msg = env->GetArrayLength(jroles);
    std::vector<std::string> roles(n_msg), contents(n_msg);
    std::vector<llama_chat_message> msgs(n_msg);
    for (jsize i = 0; i < n_msg; i++) {
        auto r = (jstring) env->GetObjectArrayElement(jroles, i);
        auto c = (jstring) env->GetObjectArrayElement(jcontents, i);
        roles[i] = jstr(env, r);
        contents[i] = jstr(env, c);
        env->DeleteLocalRef(r);
        env->DeleteLocalRef(c);
    }
    for (jsize i = 0; i < n_msg; i++) msgs[i] = {roles[i].c_str(), contents[i].c_str()};

    const char *tmpl = llama_model_chat_template(g_model, nullptr);
    if (!tmpl) tmpl = "chatml";
    std::vector<char> buf(8192);
    int32_t len = llama_chat_apply_template(tmpl, msgs.data(), msgs.size(), true, buf.data(), (int32_t) buf.size());
    if (len > (int32_t) buf.size()) {
        buf.resize(len + 1);
        len = llama_chat_apply_template(tmpl, msgs.data(), msgs.size(), true, buf.data(), (int32_t) buf.size());
    }
    if (len < 0) {  // unknown template -> fall back to ChatML
        len = llama_chat_apply_template("chatml", msgs.data(), msgs.size(), true, buf.data(), (int32_t) buf.size());
        if (len > (int32_t) buf.size()) {
            buf.resize(len + 1);
            len = llama_chat_apply_template("chatml", msgs.data(), msgs.size(), true, buf.data(), (int32_t) buf.size());
        }
    }
    if (len < 0) { LOGE("chat template failed"); return utf8_to_jstring(env, ""); }
    const std::string prompt(buf.data(), len);

    // ---- tokenize ----
    const llama_vocab *vocab = llama_model_get_vocab(g_model);
    int n_prompt = -llama_tokenize(vocab, prompt.c_str(), (int32_t) prompt.size(), nullptr, 0, true, true);
    std::vector<llama_token> tokens(n_prompt);
    if (llama_tokenize(vocab, prompt.c_str(), (int32_t) prompt.size(), tokens.data(), n_prompt, true, true) < 0) {
        LOGE("tokenize failed");
        return utf8_to_jstring(env, "");
    }
    // keep the tail if the prompt is too long (leave room for the answer)
    const int budget = g_n_ctx - std::max(64, (int) max_tokens) - 4;
    if ((int) tokens.size() > budget && budget > 0) {
        tokens.erase(tokens.begin(), tokens.end() - budget);
    }

    // ---- fresh KV cache: Kotlin sends the full conversation each call ----
    llama_memory_clear(llama_get_memory(g_ctx), true);

    // ---- sampler ----
    auto sp = llama_sampler_chain_default_params();
    llama_sampler *smpl = llama_sampler_chain_init(sp);
    llama_sampler_chain_add(smpl, llama_sampler_init_penalties(llama_vocab_n_tokens(vocab), 64, 1.1f, 0.0f, 0.0f));
    llama_sampler_chain_add(smpl, llama_sampler_init_top_k(40));
    llama_sampler_chain_add(smpl, llama_sampler_init_top_p(0.9f, 1));
    llama_sampler_chain_add(smpl, llama_sampler_init_min_p(0.05f, 1));
    llama_sampler_chain_add(smpl, llama_sampler_init_temp(temperature));
    llama_sampler_chain_add(smpl, llama_sampler_init_dist(LLAMA_DEFAULT_SEED));

    jmethodID onToken = nullptr;
    if (callback) {
        jclass cbCls = env->GetObjectClass(callback);
        onToken = env->GetMethodID(cbCls, "onToken", "(Ljava/lang/String;)Z");
        env->DeleteLocalRef(cbCls);
    }

    // ---- prefill in chunks of n_batch ----
    for (size_t i = 0; i < tokens.size(); i += 512) {
        int32_t n = (int32_t) std::min<size_t>(512, tokens.size() - i);
        if (llama_decode(g_ctx, llama_batch_get_one(tokens.data() + i, n)) != 0) {
            LOGE("prefill decode failed");
            llama_sampler_free(smpl);
            return utf8_to_jstring(env, "");
        }
    }

    // ---- generate ----
    std::string out, pending;
    char piece[256];
    for (int i = 0; i < max_tokens && !g_stop; i++) {
        llama_token tok = llama_sampler_sample(smpl, g_ctx, -1);
        if (llama_vocab_is_eog(vocab, tok)) break;

        int n = llama_token_to_piece(vocab, tok, piece, sizeof(piece), 0, false);
        if (n > 0) {
            pending.append(piece, n);
            size_t ok = valid_utf8_prefix(pending);
            if (ok > 0) {
                std::string chunk = pending.substr(0, ok);
                pending.erase(0, ok);
                out += chunk;
                if (onToken) {
                    jstring js = utf8_to_jstring(env, chunk);
                    jboolean keep = env->CallBooleanMethod(callback, onToken, js);
                    env->DeleteLocalRef(js);
                    if (env->ExceptionCheck()) { env->ExceptionClear(); break; }
                    if (!keep) break;
                }
            }
        }

        if (llama_decode(g_ctx, llama_batch_get_one(&tok, 1)) != 0) break;  // ctx full or aborted
    }
    llama_sampler_free(smpl);
    return utf8_to_jstring(env, out);
}

extern "C" JNIEXPORT void JNICALL
Java_com_skyverse_app_core_ai_LlamaEngine_stopJni(JNIEnv *, jobject) {
    g_stop = true;
}

extern "C" JNIEXPORT void JNICALL
Java_com_skyverse_app_core_ai_LlamaEngine_freeModelJni(JNIEnv *, jobject) {
    g_stop = true;
    std::lock_guard<std::mutex> lock(g_mutex);
    free_all();
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_skyverse_app_core_ai_LlamaEngine_systemInfoJni(JNIEnv *env, jobject) {
    return utf8_to_jstring(env, llama_print_system_info());
}
