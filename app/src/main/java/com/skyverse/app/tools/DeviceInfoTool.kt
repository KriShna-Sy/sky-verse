package com.skyverse.app.tools

import android.app.ActivityManager
import android.content.Context
import android.os.Build

class DeviceInfoTool(private val context: Context) {
    fun getSystemMetrics(): DeviceMetrics {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)

        val totalRamMb = (memInfo.totalMem / (1024 * 1024)).toInt()
        val availRamMb = (memInfo.availMem / (1024 * 1024)).toInt()
        val usedRamMb = totalRamMb - availRamMb

        return DeviceMetrics(
            totalRamMb = totalRamMb,
            usedRamMb = usedRamMb,
            availableRamMb = availRamMb,
            osVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
            cpuCores = Runtime.getRuntime().availableProcessors()
        )
    }
}

data class DeviceMetrics(
    val totalRamMb: Int,
    val usedRamMb: Int,
    val availableRamMb: Int,
    val osVersion: String,
    val deviceModel: String,
    val cpuCores: Int
)
