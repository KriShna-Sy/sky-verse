package com.skyverse.app.core.security

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SecurityPolicyManagerTest {

    private lateinit var policyManager: SecurityPolicyManager

    @Before
    fun setUp() {
        policyManager = SecurityPolicyManager()
    }

    @Test
    fun testSafeActionsPass() {
        val result = policyManager.validateAction("READ_BATTERY")
        assertEquals(ActionSafetyLevel.SAFE, result.level)
    }

    @Test
    fun testDestructiveActionsRequireConfirmation() {
        val result = policyManager.validateAction("CLEAR_ALL_MEMORIES")
        assertEquals(ActionSafetyLevel.REQUIRES_CONFIRMATION, result.level)
    }

    @Test
    fun testCloudSyncBlockedByAirGapPolicy() {
        val result = policyManager.validateAction("CLOUD_SYNC")
        assertEquals(ActionSafetyLevel.BLOCKED_BY_POLICY, result.level)
    }
}
