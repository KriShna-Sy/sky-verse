package com.skyverse.app.core.security

enum class ActionSafetyLevel {
    SAFE,
    REQUIRES_CONFIRMATION,
    BLOCKED_BY_POLICY
}

data class SafetyCheckResult(
    val level: ActionSafetyLevel,
    val warningMessage: String? = null
)

class SecurityPolicyManager {
    fun validateAction(actionName: String): SafetyCheckResult {
        return when (actionName.uppercase()) {
            "READ_BATTERY", "TOGGLE_FLASHLIGHT", "GET_DEVICE_INFO", "READ_MEMORY", "LAUNCH_APP" -> {
                SafetyCheckResult(ActionSafetyLevel.SAFE)
            }
            "CLEAR_ALL_MEMORIES", "DELETE_MEMORY", "DELETE_FILES", "SYSTEM_RESET" -> {
                SafetyCheckResult(
                    ActionSafetyLevel.REQUIRES_CONFIRMATION,
                    "This action will permanently alter or remove local data. Confirmation required."
                )
            }
            "CLOUD_SYNC", "EXTERNAL_API_CALL", "SEND_TELEMETRY" -> {
                SafetyCheckResult(
                    ActionSafetyLevel.BLOCKED_BY_POLICY,
                    "Action blocked by Sky Verse Privacy & Offline Policy."
                )
            }
            else -> SafetyCheckResult(ActionSafetyLevel.SAFE)
        }
    }
}
