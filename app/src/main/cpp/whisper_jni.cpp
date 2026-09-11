#include <jni.h>
#include <string>

extern "C" JNIEXPORT jboolean JNICALL
Java_com_skyverse_app_core_voice_WhisperEngine_initWhisperJni(
        JNIEnv* env,
        jobject /* this */,
        jstring modelPath) {
    // TODO: Implement real whisper_init_from_file
    return JNI_TRUE;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_skyverse_app_core_voice_WhisperEngine_transcribeAudioJni(
        JNIEnv* env,
        jobject /* this */,
        jfloatArray audioData) {
    // TODO: Implement whisper_full and extract text
    std::string text = "Turn on the flashlight.";
    return env->NewStringUTF(text.c_str());
}

extern "C" JNIEXPORT void JNICALL
Java_com_skyverse_app_core_voice_WhisperEngine_freeWhisperJni(
        JNIEnv* env,
        jobject /* this */) {
    // TODO: Implement whisper_free
}
