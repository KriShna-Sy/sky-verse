package com.skyverse.app.core.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionManager(private val context: Context) {
    fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun getPrivacyOverview(): PrivacyOverview {
        return PrivacyOverview(
            microphoneGranted = checkPermission(Manifest.permission.RECORD_AUDIO),
            cameraGranted = checkPermission(Manifest.permission.CAMERA),
            calendarGranted = checkPermission(Manifest.permission.READ_CALENDAR),
            contactsGranted = checkPermission(Manifest.permission.READ_CONTACTS),
            notificationsGranted = checkPermission("android.permission.POST_NOTIFICATIONS"),
            internetAccess = false, // Strictly OFF by design
            cloudAiEnabled = false, // Strictly OFF by design
            analyticsEnabled = false, // Strictly OFF by design
            telemetryEnabled = false // Strictly OFF by design
        )
    }
}

data class PrivacyOverview(
    val microphoneGranted: Boolean,
    val cameraGranted: Boolean,
    val calendarGranted: Boolean,
    val contactsGranted: Boolean,
    val notificationsGranted: Boolean,
    val internetAccess: Boolean,
    val cloudAiEnabled: Boolean,
    val analyticsEnabled: Boolean,
    val telemetryEnabled: Boolean
)
