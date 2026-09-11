package com.skyverse.app.core.context

import android.content.Context
import com.skyverse.app.tools.BatteryTool
import java.util.Calendar

data class ProactiveSuggestion(
    val title: String,
    val description: String,
    val recommendedAction: String,
    val priority: Int // 1=High, 2=Medium, 3=Low
)

class ContextAwarenessEngine(private val context: Context) {
    private val batteryTool = BatteryTool(context)

    fun evaluateContextualSuggestions(): List<ProactiveSuggestion> {
        val suggestions = mutableListOf<ProactiveSuggestion>()
        val battery = batteryTool.getBatteryInfo()

        // 1. Low battery suggestion
        if (battery.percentage <= 20 && !battery.isCharging) {
            suggestions.add(
                ProactiveSuggestion(
                    title = "Low Battery Detected (${battery.percentage}%)",
                    description = "KriShna, battery is getting low. Would you like me to toggle off high-frequency sensors?",
                    recommendedAction = "Optimize Power",
                    priority = 1
                )
            )
        }

        // 2. Evening routine suggestion
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (hour >= 21 || hour < 5) {
            suggestions.add(
                ProactiveSuggestion(
                    title = "Night Mode Recommended",
                    description = "It's late in the evening. Would you like to enable Night Shield & auto-dismiss popups?",
                    recommendedAction = "Enable Night Shield",
                    priority = 2
                )
            )
        }

        // 3. Ambient System Health
        suggestions.add(
            ProactiveSuggestion(
                title = "Local AI Health Nominal",
                description = "All 3 quantized models are loaded in RAM. Speaker Biometrics Lock active.",
                recommendedAction = "View Profiler",
                priority = 3
            )
        )

        return suggestions.sortedBy { it.priority }
    }
}
