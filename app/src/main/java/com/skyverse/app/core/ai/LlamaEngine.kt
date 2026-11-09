package com.skyverse.app.core.ai

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Process-wide llama.cpp engine (one model in RAM, shared by every screen).
 * Native side: app/src/main/cpp/llama_jni.cpp
 */
object LlamaEngine {

    fun interface TokenCallback {
        fun onToken(piece: String): Boolean   // return false to stop
    }

    sealed class LoadResult {
        data class Ready(val path: String) : LoadResult()
        data class Failed(val reason: String) : LoadResult()
    }

    private const val TAG = "SkyLlama"

    private external fun loadModelJni(modelPath: String, nCtx: Int): Boolean
    private external fun generateJni(
        roles: Array<String>,
        contents: Array<String>,
        maxTokens: Int,
        temperature: Float,
        callback: TokenCallback?
    ): String
    private external fun stopJni()
    private external fun freeModelJni()
    private external fun systemInfoJni(): String

    val isNativeAvailable: Boolean = try {
        System.loadLibrary("skyverse_jni"); true
    } catch (e: UnsatisfiedLinkError) {
        Log.e(TAG, "libskyverse_jni.so missing — native build did not run", e); false
    }

    private val loadMutex = Mutex()

    @Volatile var loadedModelPath: String? = null
        private set
    @Volatile var lastLoadTimeMs: Long = 0
        private set

    /** Loads the active model (from LocalModelManager) if it isn't loaded yet. */
    suspend fun ensureLoaded(context: Context): LoadResult = loadMutex.withLock {
        if (!isNativeAvailable) return LoadResult.Failed("Native llama library not built into this APK.")
        val manager = LocalModelManager(context)
        val file: File = manager.activeModelFile()
            ?: return LoadResult.Failed(
                "No model found. Push a GGUF to:\n${manager.modelDirs().first().absolutePath}\n" +
                    "or use Settings → Import model."
            )
        if (loadedModelPath == file.absolutePath) return LoadResult.Ready(file.absolutePath)

        val nCtx = getDeviceRamTier(context).maxTokens.coerceIn(1024, 4096)
        return withContext(Dispatchers.IO) {
            val t0 = System.currentTimeMillis()
            val ok = loadModelJni(file.absolutePath, nCtx)
            lastLoadTimeMs = System.currentTimeMillis() - t0
            if (ok) {
                loadedModelPath = file.absolutePath
                Log.i(TAG, "Loaded ${file.name} in ${lastLoadTimeMs}ms (n_ctx=$nCtx)")
                LoadResult.Ready(file.absolutePath)
            } else {
                loadedModelPath = null
                LoadResult.Failed("Failed to load ${file.name} (corrupt file or not enough RAM).")
            }
        }
    }

    /**
     * Streams generated text pieces. messages = list of (role, content), role in system|user|assistant.
     * Cancelling the collector stops native generation.
     */
    fun generate(
        messages: List<Pair<String, String>>,
        maxTokens: Int = 256,
        temperature: Float = 0.7f
    ): Flow<String> = callbackFlow {
        val roles = messages.map { it.first }.toTypedArray()
        val contents = messages.map { it.second }.toTypedArray()
        val finished = AtomicBoolean(false)
        launch(Dispatchers.Default) {
            try {
                generateJni(roles, contents, maxTokens, temperature) { piece ->
                    trySend(piece).isSuccess   // fails only if the collector went away
                }
            } finally {
                finished.set(true)
                channel.close()
            }
        }
        awaitClose { if (!finished.get()) stopJni() }
    }.buffer(Channel.UNLIMITED)

    fun stop() {
        if (isNativeAvailable) stopJni()
    }

    fun unload() {
        if (isNativeAvailable) freeModelJni()
        loadedModelPath = null
    }

    fun systemInfo(): String = if (isNativeAvailable) systemInfoJni() else "native library missing"
}
