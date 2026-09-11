package com.skyverse.app.core.voice

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Always-on keyword spotting using OpenWakeWord (ONNX).
 * Very lightweight. Detects "Hey Sky" continuously.
 */
class WakeWordEngine {

    private var isLoaded = false

    suspend fun loadModel(modelPath: String): Boolean = withContext(Dispatchers.IO) {
        if (isLoaded) return@withContext true
        // TODO: Load ONNX Runtime session for OpenWakeWord
        isLoaded = true
        return@withContext isLoaded
    }

    /**
     * Processes sliding window audio.
     * Returns true if "Hey Sky" is detected.
     */
    fun processAudioChunk(audioChunk: FloatArray): Boolean {
        // TODO: Run ONNX inference
        // Simulated
        return false 
    }

    fun unload() {
        if (isLoaded) {
            // TODO: Close ONNX session
            isLoaded = false
        }
    }
}
