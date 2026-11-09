package com.skyverse.app.core.voice

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

/**
 * Speech-to-text using Android's recognizer.
 * Android 13+: uses the fully on-device recognizer when its language pack is installed;
 * otherwise falls back to the default recognizer with EXTRA_PREFER_OFFLINE.
 * Must be used from the main thread.
 */
class SpeechInput(context: Context) {
    private val appContext = context.applicationContext
    private var recognizer: SpeechRecognizer? = null
    private var triedOnDevice = false

    var onPartial: (String) -> Unit = {}
    var onFinal: (String) -> Unit = {}
    var onErrorMessage: (String) -> Unit = {}
    var onListeningChanged: (Boolean) -> Unit = {}

    val isAvailable: Boolean get() = SpeechRecognizer.isRecognitionAvailable(appContext)

    fun start() {
        stop()
        val useOnDevice = !triedOnDevice && Build.VERSION.SDK_INT >= 33 &&
            SpeechRecognizer.isOnDeviceRecognitionAvailable(appContext)
        recognizer = (if (useOnDevice) SpeechRecognizer.createOnDeviceSpeechRecognizer(appContext)
                      else SpeechRecognizer.createSpeechRecognizer(appContext)).apply {
            setRecognitionListener(listener(useOnDevice))
            startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            })
        }
        onListeningChanged(true)
    }

    fun stop() {
        recognizer?.run { stopListening(); cancel(); destroy() }
        recognizer = null
        onListeningChanged(false)
    }

    private fun listener(onDevice: Boolean) = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onEvent(eventType: Int, params: Bundle?) {}

        override fun onPartialResults(partialResults: Bundle?) {
            partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()?.takeIf { it.isNotBlank() }?.let(onPartial)
        }

        override fun onResults(results: Bundle?) {
            val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
            stop()
            if (text.isNotBlank()) onFinal(text) else onErrorMessage("Didn't catch that.")
        }

        override fun onError(error: Int) {
            stop()
            // On-device pack missing -> retry once with the default recognizer
            if (onDevice && (error == ERROR_LANGUAGE_NOT_SUPPORTED || error == ERROR_LANGUAGE_UNAVAILABLE)) {
                triedOnDevice = true
                start()
                return
            }
            onErrorMessage(
                when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH, SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Didn't catch that."
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission denied."
                    SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                        "Offline speech pack not installed (Settings → System → Languages → On-device speech recognition)."
                    else -> "Speech recognizer error $error."
                }
            )
        }
    }

    fun destroy() = stop()

    private companion object {
        // SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED / ERROR_LANGUAGE_UNAVAILABLE (API 31)
        const val ERROR_LANGUAGE_NOT_SUPPORTED = 12
        const val ERROR_LANGUAGE_UNAVAILABLE = 13
    }
}
