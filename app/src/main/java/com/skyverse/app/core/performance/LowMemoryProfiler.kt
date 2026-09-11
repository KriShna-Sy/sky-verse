package com.skyverse.app.core.performance

import android.app.ActivityManager
import android.content.Context

data class MemoryProfile(
    val totalRamMb: Long,
    val availableRamMb: Long,
    val isLowRamDevice: Boolean,
    val isLowMemoryState: Boolean,
    val recommendedQuantization: String,
    val maxModelMemoryMb: Int
)

class LowMemoryProfiler(private val context: Context) {

    fun getDeviceMemoryProfile(): MemoryProfile {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalRamMb = memoryInfo.totalMem / (1024 * 1024)
        val availableRamMb = memoryInfo.availMem / (1024 * 1024)
        val isLowRamDevice = activityManager.isLowRamDevice || totalRamMb <= 3072 // <= 3GB RAM

        return MemoryProfile(
            totalRamMb = totalRamMb,
            availableRamMb = availableRamMb,
            isLowRamDevice = isLowRamDevice,
            isLowMemoryState = memoryInfo.lowMemory,
            recommendedQuantization = if (isLowRamDevice) "INT4 Quantized (Ultra-Light 280MB)" else "INT8 Quantized (Standard 550MB)",
            maxModelMemoryMb = if (isLowRamDevice) 300 else 600
        )
    }

    /**
     * Forces immediate garbage collection and trims memory caches for older devices
     */
    fun performMemoryOptimizationTrim() {
        System.gc()
        Runtime.getRuntime().gc()
    }
}
