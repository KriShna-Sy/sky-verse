package com.skyverse.app.core.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

enum class VoiceState {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING
}

/** Offline text-to-speech (Android system TTS engine). */
class SkyVoicePipeline(context: Context) : TextToSpeech.OnInitListener {
    private val tts: TextToSpeech = TextToSpeech(context.applicationContext, this)
    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState

    @Volatile private var isTtsReady = false
    @Volatile private var pending: String? = null
    private var onDone: (() -> Unit)? = null

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return
        val lang = tts.setLanguage(Locale.getDefault())
        if (lang == TextToSpeech.LANG_MISSING_DATA || lang == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts.setLanguage(Locale.US)
        }
        tts.setSpeechRate(0.95f)
        tts.setPitch(1.05f)
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) { _voiceState.value = VoiceState.SPEAKING }
            override fun onDone(utteranceId: String?) { finish() }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) { finish() }
            override fun onStop(utteranceId: String?, interrupted: Boolean) { finish() }
        })
        isTtsReady = true
        pending?.let { pending = null; speak(it, onDone) }
    }

    private fun finish() {
        _voiceState.value = VoiceState.IDLE
        onDone?.invoke()
        onDone = null
    }

    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        val clean = text.replace(Regex("[*#`_>•⚡🧠🔒📄📁🖼️⛔⚠️]"), " ").trim()
        if (clean.isBlank()) { onComplete?.invoke(); return }
        onDone = onComplete
        if (!isTtsReady) { pending = clean; return }
        tts.speak(clean, TextToSpeech.QUEUE_FLUSH, null, "SKY_TTS_${System.nanoTime()}")
    }

    fun stopSpeaking() {
        if (isTtsReady) tts.stop()
        _voiceState.value = VoiceState.IDLE
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}
