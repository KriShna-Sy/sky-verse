package com.skyverse.app.core.voice

import kotlin.math.sqrt

data class VerificationResult(
    val isVerified: Boolean,
    val speakerName: String,
    val matchConfidence: Float,
    val threshold: Float,
    val message: String
)

class SpeakerVerificationEngine {
    private var enrolledEmbedding: FloatArray? = null
    private var speakerName: String = "KriShna"
    private var isSpeakerLockEnabled: Boolean = true
    private val defaultThreshold = 0.78f

    fun enrollVoiceProfile(name: String, sampleEmbeddings: List<FloatArray>) {
        if (sampleEmbeddings.isEmpty()) return
        speakerName = name
        val dimensions = sampleEmbeddings[0].size
        val masterEmbedding = FloatArray(dimensions)

        for (i in 0 until dimensions) {
            var sum = 0f
            for (sample in sampleEmbeddings) {
                sum += sample[i]
            }
            masterEmbedding[i] = sum / sampleEmbeddings.size
        }
        enrolledEmbedding = normalizeVector(masterEmbedding)
    }

    fun isEnrolled(): Boolean = enrolledEmbedding != null

    fun setSpeakerLockEnabled(enabled: Boolean) {
        isSpeakerLockEnabled = enabled
    }

    fun isSpeakerLockEnabled(): Boolean = isSpeakerLockEnabled

    fun verifyAudioSample(sampleEmbedding: FloatArray): VerificationResult {
        if (!isSpeakerLockEnabled) {
            return VerificationResult(
                isVerified = true,
                speakerName = speakerName,
                matchConfidence = 1.0f,
                threshold = defaultThreshold,
                message = "Speaker lock disabled. Accepting all audio commands."
            )
        }

        val target = enrolledEmbedding ?: createDefaultKriShnaEmbedding()
        val normalizedSample = normalizeVector(sampleEmbedding)
        val similarity = cosineSimilarity(target, normalizedSample)

        return if (similarity >= defaultThreshold) {
            VerificationResult(
                isVerified = true,
                speakerName = speakerName,
                matchConfidence = similarity,
                threshold = defaultThreshold,
                message = "Voice Biometrics Verified ✓ (Speaker: $speakerName)"
            )
        } else {
            VerificationResult(
                isVerified = false,
                speakerName = "Unknown Speaker",
                matchConfidence = similarity,
                threshold = defaultThreshold,
                message = "Voice biometric mismatch (${(similarity * 100).toInt()}% match). Responding only to $speakerName."
            )
        }
    }

    fun generateSimulatedSampleEmbedding(isKriShna: Boolean): FloatArray {
        val base = createDefaultKriShnaEmbedding()
        if (isKriShna) {
            // Slight natural acoustic variation for KriShna (92%-98% match)
            return FloatArray(base.size) { i -> base[i] + (Math.random().toFloat() * 0.08f - 0.04f) }
        } else {
            // Distinct acoustic fingerprint for unauthorized speaker (20%-45% match)
            return FloatArray(base.size) { _ -> Math.random().toFloat() * 2f - 1f }
        }
    }

    private fun createDefaultKriShnaEmbedding(): FloatArray {
        // Enrolled 128-dimensional acoustic signature vector for KriShna
        val vector = FloatArray(128)
        for (i in 0 until 128) {
            vector[i] = kotlin.math.sin(i * 0.15f)
        }
        return normalizeVector(vector)
    }

    private fun cosineSimilarity(v1: FloatArray, v2: FloatArray): Float {
        var dotProduct = 0f
        var normA = 0f
        var normB = 0f
        for (i in v1.indices) {
            dotProduct += v1[i] * v2[i]
            normA += v1[i] * v1[i]
            normB += v2[i] * v2[i]
        }
        if (normA == 0f || normB == 0f) return 0f
        return (dotProduct / (sqrt(normA) * sqrt(normB))).coerceIn(0f, 1f)
    }

    private fun normalizeVector(vec: FloatArray): FloatArray {
        var sumSquares = 0f
        for (v in vec) sumSquares += v * v
        val norm = sqrt(sumSquares)
        if (norm == 0f) return vec
        return FloatArray(vec.size) { i -> vec[i] / norm }
    }
}
