package com.skyverse.app.core.ai

enum class IntentType {
    BATTERY_QUERY,
    FLASHLIGHT_ON,
    FLASHLIGHT_OFF,
    DEVICE_INFO,
    APP_LAUNCH,
    MEDIA_PLAY,
    SYSTEM_SETTINGS,
    ACTION_CONFIRMATION,
    PRONOUN_ACTION,
    FILE_ACCESS_QUERY,
    FILE_SEARCH_AND_OPEN,
    MEMORY_REMEMBER,
    MEMORY_QUERY,
    MEMORY_FORGET,
    PRIVACY_QUERY,
    LLM_REASONING
}

data class ParsedIntent(
    val type: IntentType,
    val extractedArg: String? = null,
    val confidence: Float = 1.0f,
    val isDeterministic: Boolean = true
)

class IntentRouter {
    fun routeQuery(rawQuery: String): ParsedIntent {
        val q = rawQuery.lowercase().trim()

        return when {
            // File access / file tracer query: "access my files", "show my files", "file manager"
            q.contains("access my files") || q.contains("acces my files") || q.contains("show my files") || q.contains("explore files") || q.contains("file manager") -> {
                ParsedIntent(IntentType.FILE_ACCESS_QUERY)
            }
            // Pronoun contextual action: "turn it on now", "turn it off", "switch it on"
            q.contains("turn it on") || q.contains("turn it off") || q.contains("switch it on") || q.contains("switch it off") || q == "turn it on now" -> {
                val isTurnOn = q.contains("on")
                ParsedIntent(IntentType.PRONOUN_ACTION, extractedArg = if (isTurnOn) "on" else "off")
            }
            // Flashlight ON
            q.contains("turn on flashlight") || q.contains("flashlight on") || q.contains("turn on torch") || q.contains("torch on") -> {
                ParsedIntent(IntentType.FLASHLIGHT_ON)
            }
            // Flashlight OFF
            q.contains("turn off flashlight") || q.contains("flashlight off") || q.contains("turn off torch") || q.contains("torch off") -> {
                ParsedIntent(IntentType.FLASHLIGHT_OFF)
            }
            // Battery checks
            q.contains("battery") || q.contains("charge") || q.contains("power level") -> {
                ParsedIntent(IntentType.BATTERY_QUERY)
            }
            // Device specs / RAM
            q.contains("device info") || q.contains("system metrics") || q.contains("ram status") || q.contains("cpu status") || q.contains("device specs") -> {
                ParsedIntent(IntentType.DEVICE_INFO)
            }
            // Memory storage
            q.contains("remember that") || q.startsWith("remember ") -> {
                val fact = q.substringAfter("remember ").removePrefix("that ")
                ParsedIntent(IntentType.MEMORY_REMEMBER, extractedArg = fact)
            }
            // Memory query
            q.contains("what do you remember") || q.contains("my memories") || q.contains("show memories") -> {
                ParsedIntent(IntentType.MEMORY_QUERY)
            }
            // Memory forget
            q.contains("forget that") || q.contains("clear all memories") || q.contains("wipe memory") -> {
                ParsedIntent(IntentType.MEMORY_FORGET)
            }
            // Privacy query
            q.contains("privacy") || q.contains("local mode") || q.contains("is cloud off") -> {
                ParsedIntent(IntentType.PRIVACY_QUERY)
            }
            // Specific PDF search & open request (e.g. "open it", "open that pdf", "trace mae brochure pdf", "find file adhaar")
            (q == "open it" || q == "open it now" || q == "open that pdf" || q == "open that pdf now" ||
             q.contains("open it") || q.contains("with drive") || q.contains("with google drive") ||
             ((q.contains("open") || q.contains("find") || q.contains("search") || q.contains("trace")) &&
              (q.contains("pdf") || q.contains("brochure") || q.contains("adhaar") || q.contains("file") || q.contains("doc")))) -> {
                ParsedIntent(IntentType.FILE_SEARCH_AND_OPEN, extractedArg = q)
            }
            // YouTube / Media play intent
            q.contains("play") || q.contains("song") || q.contains("utube") || q.contains("youtube") || q.contains("video") -> {
                ParsedIntent(IntentType.MEDIA_PLAY, extractedArg = q)
            }
            // Wi-Fi / Bluetooth / Settings toggles
            q.contains("wifi") || q.contains("wi-fi") || q.contains("bluetooth") || q.contains("volume") || q.contains("brightness") || q.contains("airplane") || q.contains("hotspot") -> {
                ParsedIntent(IntentType.SYSTEM_SETTINGS, extractedArg = q)
            }
            // Direct action request: "do the action", "do what I asked"
            q.contains("do the action") || q.contains("do what i asked") || q.contains("do it") || q.contains("do that") -> {
                ParsedIntent(IntentType.ACTION_CONFIRMATION, extractedArg = q)
            }
            // App launcher
            q.startsWith("open ") || q.startsWith("launch ") -> {
                val appName = q.removePrefix("open ").removePrefix("launch ")
                ParsedIntent(IntentType.APP_LAUNCH, extractedArg = appName)
            }
            // Default to local LLM reasoning
            else -> {
                ParsedIntent(IntentType.LLM_REASONING, isDeterministic = false)
            }
        }
    }
}
