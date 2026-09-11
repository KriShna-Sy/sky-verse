package com.skyverse.app.core.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IntentRouterTest {

    private lateinit var intentRouter: IntentRouter

    @Before
    fun setUp() {
        intentRouter = IntentRouter()
    }

    @Test
    fun testBatteryQueryRouting() {
        val result = intentRouter.routeQuery("What is my battery level?")
        assertEquals(IntentType.BATTERY_QUERY, result.type)
        assertTrue(result.isDeterministic)
    }

    @Test
    fun testFlashlightRouting() {
        val onResult = intentRouter.routeQuery("turn on flashlight")
        assertEquals(IntentType.FLASHLIGHT_ON, onResult.type)

        val offResult = intentRouter.routeQuery("turn off torch")
        assertEquals(IntentType.FLASHLIGHT_OFF, offResult.type)
    }

    @Test
    fun testFileAccessRouting() {
        val result = intentRouter.routeQuery("can u access my files")
        assertEquals(IntentType.FILE_ACCESS_QUERY, result.type)
    }

    @Test
    fun testFileSearchAndOpenRouting() {
        val result = intentRouter.routeQuery("open that pdf now")
        assertEquals(IntentType.FILE_SEARCH_AND_OPEN, result.type)
    }

    @Test
    fun testGeneralDocumentQueryRoutesToLLMReasoning() {
        val result = intentRouter.routeQuery("What is a PDF file format?")
        assertEquals(IntentType.LLM_REASONING, result.type)
        assertFalse(result.isDeterministic)
    }

    @Test
    fun testMemoryRememberRouting() {
        val result = intentRouter.routeQuery("remember that my favorite color is cyan")
        assertEquals(IntentType.MEMORY_REMEMBER, result.type)
        assertEquals("my favorite color is cyan", result.extractedArg)
    }

    @Test
    fun testPrivacyQueryRouting() {
        val result = intentRouter.routeQuery("show my privacy status")
        assertEquals(IntentType.PRIVACY_QUERY, result.type)
    }
}
