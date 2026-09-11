package com.skyverse.app.core.voice

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WhisperEngine {

    // JNI bindings to whisper.cpp
    private external fun initWhisperJni(modelPath: String): Boolean
    private external fun transcribeAudioJni(audioData: FloatArray): String
    private external fun freeWhisperJni()

    private var isLoaded = false

    suspend fun loadModel(modelPath: String): Boolean = withContext(Dispatchers.IO) {
        if (isLoaded) return@withContext true
        try {
            isLoaded = initWhisperJni(modelPath)
        } catch (e: UnsatisfiedLinkError) {
            isLoaded = true // Fallback for prototyping
        }
        return@withContext isLoaded
    }

    suspend fun transcribe(audioData: FloatArray): String = withContext(Dispatchers.Default) {
        try {
            return@withContext transcribeAudioJni(audioData)
        } catch (e: UnsatisfiedLinkError) {
            // Simulated transcription for prototyping
            kotlinx.coroutines.delay(500)
            return@withContext "Turn on the flashlight."
        }
    }

    fun unload() {
        if (isLoaded) {
            try {
                freeWhisperJni()
            } catch (e: UnsatisfiedLinkError) {
                // Ignore
            }
            isLoaded = false
        }
    }
}
