package com.skyverse.app.core.voice

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class EnrollmentStep {
    NOT_STARTED,
    SAMPLE_1,
    SAMPLE_2,
    SAMPLE_3,
    COMPLETED
}

data class EnrollmentState(
    val step: EnrollmentStep = EnrollmentStep.NOT_STARTED,
    val promptPhrase: String = "Say 'Hey Sky, I am KriShna'",
    val samplesRecorded: Int = 0,
    val isComplete: Boolean = false
)

class VoiceEnrollmentManager(private val verificationEngine: SpeakerVerificationEngine) {
    private val _enrollmentState = MutableStateFlow(EnrollmentState())
    val enrollmentState: StateFlow<EnrollmentState> = _enrollmentState

    private val recordedSamples = mutableListOf<FloatArray>()

    fun startEnrollment() {
        recordedSamples.clear()
        _enrollmentState.value = EnrollmentState(
            step = EnrollmentStep.SAMPLE_1,
            promptPhrase = "Say: \"Hey Sky, I am KriShna.\"",
            samplesRecorded = 0,
            isComplete = false
        )
    }

    fun recordSample(): EnrollmentState {
        val sample = verificationEngine.generateSimulatedSampleEmbedding(isKriShna = true)
        recordedSamples.add(sample)

        val nextState = when (_enrollmentState.value.step) {
            EnrollmentStep.SAMPLE_1 -> EnrollmentState(
                step = EnrollmentStep.SAMPLE_2,
                promptPhrase = "Say: \"Sky, turn on flashlight and check battery.\"",
                samplesRecorded = 1
            )
            EnrollmentStep.SAMPLE_2 -> EnrollmentState(
                step = EnrollmentStep.SAMPLE_3,
                promptPhrase = "Say: \"Sky, keep all my memory private.\"",
                samplesRecorded = 2
            )
            EnrollmentStep.SAMPLE_3, EnrollmentStep.COMPLETED -> {
                verificationEngine.enrollVoiceProfile("KriShna", recordedSamples)
                EnrollmentState(
                    step = EnrollmentStep.COMPLETED,
                    promptPhrase = "Voice Enrollment Completed! Sky will now respond ONLY to KriShna's voice.",
                    samplesRecorded = 3,
                    isComplete = true
                )
            }
            else -> EnrollmentState()
        }

        _enrollmentState.value = nextState
        return nextState
    }

    fun resetEnrollment() {
        recordedSamples.clear()
        _enrollmentState.value = EnrollmentState()
    }
}
