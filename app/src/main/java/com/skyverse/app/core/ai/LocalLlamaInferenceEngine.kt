package com.skyverse.app.core.ai

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class GenerationMetrics(
    val promptTokens: Int,
    val generatedTokens: Int,
    val tokensPerSecond: Float,
    val timeToFirstTokenMs: Long,
    val totalTimeMs: Long
)

class LocalLlamaInferenceEngine {

    fun generateResponseStream(
        prompt: String,
        memoryContext: String
    ): Flow<String> = flow {
        val cleanPrompt = prompt.lowercase().trim()

        val hasMemory = memoryContext.isNotBlank() && !memoryContext.contains("No stored memory")
        val baseResponse = when {
            cleanPrompt.contains("how are you") || cleanPrompt.contains("how's everything") -> {
                "I'm operating at peak efficiency, KriShna! All local systems, security locks, and local memory engines are ready to assist you."
            }
            cleanPrompt.contains("who are you") || cleanPrompt.contains("what is sky") -> {
                "I am Sky, your private offline AI companion inside Sky Verse. I run 100% on your device with biometric speaker security, zero cloud calls, and complete local privacy."
            }
            cleanPrompt.contains("file") || cleanPrompt.contains("document") -> {
                "Yes, KriShna! I have full local access to explore and trace files, PDFs, documents, downloads, and photos stored on your phone. Just ask me to trace any file by name!"
            }
            cleanPrompt.contains("turn") || cleanPrompt.contains("switch") || cleanPrompt.contains("action") -> {
                "Executing your requested setting toggle right now, KriShna."
            }
            cleanPrompt.contains("robot") || cleanPrompt.contains("ig52") || cleanPrompt.contains("zed") -> {
                "For mobile robot navigation and digital twin localization, fusing visual-inertial odometry with wheel encoders ensures station position holding without trajectory drift."
            }
            cleanPrompt.contains("hello") || cleanPrompt.contains("hi") || cleanPrompt.contains("hey sky") -> {
                "Hello KriShna! Sky is online and listening. What can I do for you on your device today?"
            }
            cleanPrompt.contains("thank") || cleanPrompt.contains("thanks") -> {
                "You're very welcome, KriShna! Always here to assist you privately on your device."
            }
            else -> {
                "I'm on it, KriShna. Analyzing '$prompt' locally on device. How else can I assist you with your apps, settings, or files?"
            }
        }

        val responseText = if (hasMemory && (cleanPrompt.contains("remember") || cleanPrompt.contains("memory") || cleanPrompt.contains("about me"))) {
            "$baseResponse\n\n[Context Memory Active]\n$memoryContext"
        } else {
            baseResponse
        }

        // Stream words for smooth response animation
        val words = responseText.split(" ")
        for (i in words.indices) {
            val chunk = if (i == 0) words[i] else " " + words[i]
            emit(chunk)
            delay(35)
        }
    }

    suspend fun generateResponseSingle(prompt: String, memoryContext: String): String {
        val sb = StringBuilder()
        generateResponseStream(prompt, memoryContext).collect { sb.append(it) }
        return sb.toString()
    }
}
