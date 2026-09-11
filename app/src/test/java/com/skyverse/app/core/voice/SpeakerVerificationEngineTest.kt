package com.skyverse.app.core.voice

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SpeakerVerificationEngineTest {

    private lateinit var engine: SpeakerVerificationEngine

    @Before
    fun setUp() {
        engine = SpeakerVerificationEngine()
    }

    @Test
    fun testDefaultKriShnaSpeakerVerification() {
        val krishnaAudio = engine.generateSimulatedSampleEmbedding(isKriShna = true)
        val result = engine.verifyAudioSample(krishnaAudio)

        assertTrue("KriShna speaker audio should be verified", result.isVerified)
        assertTrue("Match confidence should exceed threshold", result.matchConfidence >= result.threshold)
    }

    @Test
    fun testUnauthorizedSpeakerRejection() {
        val strangerAudio = engine.generateSimulatedSampleEmbedding(isKriShna = false)
        val result = engine.verifyAudioSample(strangerAudio)

        assertFalse("Unauthorized speaker audio should be rejected", result.isVerified)
        assertTrue("Match confidence should be below threshold", result.matchConfidence < result.threshold)
    }

    @Test
    fun testSpeakerLockDisabled() {
        engine.setSpeakerLockEnabled(false)
        val strangerAudio = engine.generateSimulatedSampleEmbedding(isKriShna = false)
        val result = engine.verifyAudioSample(strangerAudio)

        assertTrue("When speaker lock is disabled, all audio should be accepted", result.isVerified)
    }
}
