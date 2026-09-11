package com.skyverse.app.core.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

enum class VoiceState {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING
}

class SkyVoicePipeline(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState

    private var isTtsReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            tts?.setSpeechRate(0.95f)
            tts?.setPitch(1.05f) // Friendly, calm, clear tone for Sky
            isTtsReady = true
        }
    }

    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        if (isTtsReady && text.isNotBlank()) {
            _voiceState.value = VoiceState.SPEAKING
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "SKY_TTS_ID")
            _voiceState.value = VoiceState.IDLE
            onComplete?.invoke()
        }
    }

    fun startListening() {
        _voiceState.value = VoiceState.LISTENING
    }

    fun stopListening() {
        _voiceState.value = VoiceState.IDLE
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
