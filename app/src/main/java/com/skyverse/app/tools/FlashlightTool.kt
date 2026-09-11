package com.skyverse.app.tools

import android.content.Context
import android.hardware.camera2.CameraManager

class FlashlightTool(private val context: Context) {
    private var isTorchOn = false

    fun toggleFlashlight(on: Boolean): String {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return "No camera available for flashlight."
            cameraManager.setTorchMode(cameraId, on)
            isTorchOn = on
            if (on) "Flashlight turned on." else "Flashlight turned off."
        } catch (e: Exception) {
            "Flashlight action unavailable: ${e.localizedMessage}"
        }
    }

    fun isFlashlightOn(): Boolean = isTorchOn
}
