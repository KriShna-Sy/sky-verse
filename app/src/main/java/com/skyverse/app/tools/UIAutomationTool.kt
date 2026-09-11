package com.skyverse.app.tools

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.skyverse.app.core.automation.SkyAccessibilityService

class UIAutomationTool(private val context: Context) {

    fun isAutomationEnabled(): Boolean {
        return SkyAccessibilityService.isServiceConnected
    }

    fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun triggerAutoSkipAds(): String {
        val service = SkyAccessibilityService.instance
            ?: return "Accessibility Service inactive. Please enable Sky Accessibility Service in Android Settings to auto-skip ads."

        val clickSuccess = service.clickElementWithText("Skip") || service.clickElementWithText("Skip Ad") || service.clickElementWithText("Close")
        return if (clickSuccess) {
            "Auto-clicked Skip/Close button on screen successfully ✓"
        } else {
            "Scanning screen for Ad Skip controls... Sky UI Automation Active."
        }
    }

    fun tapScreenCoordinates(x: Float, y: Float): String {
        val service = SkyAccessibilityService.instance
            ?: return "Accessibility Service inactive. Enable Sky Accessibility Service in Settings."

        service.performTapAtCoordinates(x, y)
        return "Tapped screen at ($x, $y) via Sky Accessibility Service ✓"
    }

    fun swipeScreen(startX: Float, startY: Float, endX: Float, endY: Float): String {
        val service = SkyAccessibilityService.instance
            ?: return "Accessibility Service inactive. Enable Sky Accessibility Service in Settings."

        service.performSwipe(startX, startY, endX, endY)
        return "Performed swipe gesture from ($startX, $startY) -> ($endX, $endY) ✓"
    }
}
