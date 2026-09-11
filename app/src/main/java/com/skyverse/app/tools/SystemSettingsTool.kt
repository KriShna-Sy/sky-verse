package com.skyverse.app.tools

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.skyverse.app.core.automation.SkyAccessibilityService

class SystemSettingsTool(private val context: Context) {

    fun handleSettingAction(actionQuery: String): String {
        val q = actionQuery.lowercase().trim()

        return when {
            q.contains("bluetooth") || q.contains("bt") -> {
                val turnOff = q.contains("off") || q.contains("disable") || q.contains("turn off")
                toggleBluetooth(turnOff)
            }
            q.contains("wifi") || q.contains("wi-fi") -> {
                val turnOff = q.contains("off") || q.contains("disable") || q.contains("turn off")
                toggleWifi(turnOff)
            }
            q.contains("volume") || q.contains("sound") || q.contains("mute") -> {
                openSoundSettings()
            }
            q.contains("brightness") || q.contains("display") || q.contains("screen") -> {
                openDisplaySettings()
            }
            q.contains("airplane") || q.contains("flight mode") -> {
                openAirplaneModeSettings()
            }
            q.contains("hotspot") || q.contains("tethering") -> {
                openHotspotSettings()
            }
            else -> {
                openGeneralSettings()
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun toggleBluetooth(turnOff: Boolean): String {
        // Attempt direct Bluetooth API toggle first
        try {
            val adapter = BluetoothAdapter.getDefaultAdapter()
            if (adapter != null) {
                if (turnOff && adapter.isEnabled) {
                    adapter.disable()
                    return "Disabled Bluetooth directly for KriShna ✓"
                } else if (!turnOff && !adapter.isEnabled) {
                    adapter.enable()
                    return "Enabled Bluetooth directly for KriShna ✓"
                }
            }
        } catch (e: Exception) {
            // Permission restricted by Android OS, fallback to Accessibility Auto-Toggle
        }

        // Accessibility Auto-Toggle fallback: opens setting and auto-clicks switch
        SkyAccessibilityService.pendingActionToggle = if (turnOff) "bluetooth_off" else "bluetooth_on"
        return try {
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            if (turnOff) "Toggling Bluetooth OFF for KriShna ✓" else "Toggling Bluetooth ON for KriShna ✓"
        } catch (e: Exception) {
            "Could not toggle Bluetooth: ${e.localizedMessage}"
        }
    }

    private fun toggleWifi(turnOff: Boolean): String {
        SkyAccessibilityService.pendingActionToggle = if (turnOff) "wifi_off" else "wifi_on"
        return try {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            if (turnOff) "Toggling Wi-Fi OFF for KriShna ✓" else "Toggling Wi-Fi ON for KriShna ✓"
        } catch (e: Exception) {
            "Could not toggle Wi-Fi: ${e.localizedMessage}"
        }
    }

    private fun openSoundSettings(): String {
        return try {
            val intent = Intent(Settings.ACTION_SOUND_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            "Opening Sound & Volume Controls."
        } catch (e: Exception) {
            "Could not open Sound Settings."
        }
    }

    private fun openDisplaySettings(): String {
        return try {
            val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            "Opening Display & Brightness Controls."
        } catch (e: Exception) {
            "Could not open Display Settings."
        }
    }

    private fun openAirplaneModeSettings(): String {
        return try {
            val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            "Opening Airplane Mode Settings."
        } catch (e: Exception) {
            "Could not open Airplane Mode Settings."
        }
    }

    private fun openHotspotSettings(): String {
        return try {
            val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            "Opening Hotspot Settings."
        } catch (e: Exception) {
            "Could not open Hotspot Settings."
        }
    }

    private fun openGeneralSettings(): String {
        return try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            "Opening System Settings."
        } catch (e: Exception) {
            "Could not open System Settings."
        }
    }
}
