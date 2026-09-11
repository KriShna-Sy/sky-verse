package com.skyverse.app.core.ai

import android.app.ActivityManager
import android.content.Context

enum class RamTier(val maxTokens: Int, val description: String) {
    LITE(512, "4GB RAM - SmolLM2-135M"),
    STANDARD(1024, "6-8GB RAM - Qwen2.5-0.5B"),
    POWER(2048, "8-12GB RAM - Qwen2.5-1.5B"),
    ULTRA(4096, "12GB+ RAM - Qwen2.5-3B")
}

fun getDeviceRamTier(context: Context): RamTier {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)
    
    val totalRamGb = memoryInfo.totalMem / (1024.0 * 1024.0 * 1024.0)
    
    return when {
        totalRamGb < 5.0 -> RamTier.LITE
        totalRamGb < 8.0 -> RamTier.STANDARD
        totalRamGb < 12.0 -> RamTier.POWER
        else -> RamTier.ULTRA
    }
}
