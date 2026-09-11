package com.skyverse.app.core.automation

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class SkyAccessibilityService : AccessibilityService() {

    companion object {
        var instance: SkyAccessibilityService? = null
            private set

        val isServiceConnected: Boolean
            get() = instance != null

        // UI text patterns for skipping ads & closing popups
        private val adSkipKeywords = listOf(
            "skip ad",
            "skip ads",
            "skip",
            "close",
            "dismiss",
            "no thanks",
            "close ad",
            "x"
        )

        var pendingActionToggle: String? = null // e.g. "bluetooth_off", "wifi_off"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d("SkyAccessibility", "Sky Accessibility UI Automation Service Connected.")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val rootNode = rootInActiveWindow ?: return

        // 1. Check if we need to auto-toggle a setting switch (e.g. Bluetooth/Wi-Fi off)
        val currentPackage = event.packageName?.toString() ?: ""
        if (currentPackage.contains("settings") || currentPackage.contains("bluetooth") || currentPackage.contains("wifi")) {
            scanAndToggleSettingsSwitch(rootNode)
        }

        // 2. Auto-detect on-screen Ad Skip buttons & close icons
        scanAndPerformAutoSkip(rootNode)
    }

    override fun onInterrupt() {
        Log.d("SkyAccessibility", "Sky Accessibility Service Interrupted.")
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    /**
     * Automatically finds and toggles Bluetooth/Wi-Fi switches in system settings panels
     */
    private fun scanAndToggleSettingsSwitch(node: AccessibilityNodeInfo) {
        val targetToggle = pendingActionToggle ?: return
        val className = node.className?.toString() ?: ""
        val nodeText = node.text?.toString()?.lowercase() ?: ""
        val contentDesc = node.contentDescription?.toString()?.lowercase() ?: ""

        val isSwitch = className.contains("Switch") || className.contains("ToggleButton") || node.isCheckable

        if (isSwitch) {
            val settingType = targetToggle.substringBefore("_")
            val matchesSetting = nodeText.contains(settingType) || contentDesc.contains(settingType) || targetToggle.isNotEmpty()
            val isChecked = node.isChecked
            val wantOff = targetToggle.contains("off")
            val wantOn = targetToggle.contains("on")

            if (matchesSetting && ((wantOff && isChecked) || (wantOn && !isChecked))) {
                Log.d("SkyAccessibility", "Auto-toggling settings switch ($nodeText / $contentDesc): $targetToggle")
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                pendingActionToggle = null // Action complete
                return
            }
        }

        // Recursively inspect child nodes
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            scanAndToggleSettingsSwitch(child)
        }
    }

    /**
     * Recursively scans active window hierarchy for 'Skip Ad' buttons or 'X' close controls
     */
    private fun scanAndPerformAutoSkip(node: AccessibilityNodeInfo) {
        val text = node.text?.toString()?.lowercase() ?: ""
        val contentDesc = node.contentDescription?.toString()?.lowercase() ?: ""
        val viewId = node.viewIdResourceName?.lowercase() ?: ""

        val isSkipTarget = adSkipKeywords.any { keyword ->
            text.contains(keyword) || contentDesc.contains(keyword) || viewId.contains("skip") || viewId.contains("close")
        }

        if (isSkipTarget && node.isClickable) {
            Log.d("SkyAccessibility", "Auto-skipping Ad! Clicking node: text='$text', viewId='$viewId'")
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            return
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            scanAndPerformAutoSkip(child)
        }
    }

    fun performTapAtCoordinates(x: Float, y: Float, onComplete: ((Boolean) -> Unit)? = null) {
        val path = Path().apply { moveTo(x, y) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 50))
            .build()

        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                onComplete?.invoke(true)
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                onComplete?.invoke(false)
            }
        }, null)
    }

    fun performSwipe(startX: Float, startY: Float, endX: Float, endY: Float, durationMs: Long = 300) {
        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, durationMs))
            .build()

        dispatchGesture(gesture, null, null)
    }

    fun clickElementWithText(targetText: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val nodes = rootNode.findAccessibilityNodeInfosByText(targetText)
        if (!nodes.isNullOrEmpty()) {
            for (node in nodes) {
                if (node.isClickable) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    return true
                } else {
                    var parent = node.parent
                    while (parent != null) {
                        if (parent.isClickable) {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                            return true
                        }
                        parent = parent.parent
                    }
                }
            }
        }
        return false
    }
}
