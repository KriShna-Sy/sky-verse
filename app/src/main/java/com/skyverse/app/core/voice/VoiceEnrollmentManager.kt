package com.skyverse.app.core.voice

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Handles capturing user audio to generate the master voice profile embedding.
 */
class VoiceEnrollmentManager(
    private val context: Context,
    private val speakerVerificationEngine: SpeakerVerificationEngine
) {

    @SuppressLint("MissingPermission")
    suspend fun recordAndEnroll(onProgress: (Int) -> Unit): FloatArray? = withContext(Dispatchers.IO) {
        val sampleRate = 16000
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        
        // This requires RECORD_AUDIO permission, checked at UI level
        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        try {
            audioRecord.startRecording()
            
            // For now, simulate progress while "recording" 5 seconds of audio
            for (i in 1..100 step 20) {
                onProgress(i)
                delay(1000)
            }
            onProgress(100)
            
            // TODO: Actually read from buffer and pass to speakerVerificationEngine
            // For now, extract from a dummy buffer
            val dummyBuffer = FloatArray(sampleRate * 5) // 5 seconds
            return@withContext speakerVerificationEngine.extractEmbedding(dummyBuffer)
            
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        } finally {
            if (audioRecord.state == AudioRecord.STATE_INITIALIZED) {
                audioRecord.stop()
                audioRecord.release()
            }
        }
    }
}
