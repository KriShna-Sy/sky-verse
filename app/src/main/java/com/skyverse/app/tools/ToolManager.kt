package com.skyverse.app.tools

import android.content.Context
import com.skyverse.app.core.files.LocalFileTracer
import com.skyverse.app.core.vision.LocalFaceGalleryEngine

class ToolManager(private val context: Context) {
    val batteryTool = BatteryTool(context)
    val flashlightTool = FlashlightTool(context)
    val deviceInfoTool = DeviceInfoTool(context)
    val appLauncherTool = AppLauncherTool(context)
    val uiAutomationTool = UIAutomationTool(context)
    val localFileTracer = LocalFileTracer(context)
    val localFaceGalleryEngine = LocalFaceGalleryEngine(context)
    val systemSettingsTool = SystemSettingsTool(context)

    fun getBatterySummary(): String {
        val b = batteryTool.getBatteryInfo()
        return "Battery: ${b.percentage}% (${if (b.isCharging) "Charging" else "Discharging"})"
    }

    fun toggleFlashlight(enable: Boolean): String = flashlightTool.toggleFlashlight(enable)

    fun getDeviceMetricsSummary(): String {
        val m = deviceInfoTool.getSystemMetrics()
        return "Device: ${m.deviceModel} (${m.osVersion})\nRAM: ${m.usedRamMb}MB / ${m.totalRamMb}MB\nActive CPU Cores: ${m.cpuCores}"
    }

    fun launchApp(appName: String): String = appLauncherTool.launchApp(appName)

    fun autoSkipAds(): String = uiAutomationTool.triggerAutoSkipAds()

    fun handleSystemSetting(query: String): String = systemSettingsTool.handleSettingAction(query)

    fun traceFileByName(query: String): String {
        val files = localFileTracer.searchAndTraceFile(query)

        if (files.isEmpty()) return "No files or PDFs matching '$query' found on device storage."

        return "📁 Traced ${files.size} File(s) matching '$query':\n" +
                files.joinToString("\n") { "• ${it.fileName} (${it.fileType}, ${it.sizeKb}KB)\n  Path: ${it.absolutePath}" }
    }

    fun searchPhotosByPerson(personName: String): String {
        val photos = localFaceGalleryEngine.searchPhotosByPerson(personName)
        if (photos.isEmpty()) return "No tagged character photos found for '$personName'."

        return "🖼️ Found ${photos.size} Photo(s) of '$personName':\n" +
                photos.joinToString("\n") { "• ${it.photoName} (Confidence: ${(it.confidence * 100).toInt()}%)\n  Location: ${it.absolutePath}" }
    }
}
