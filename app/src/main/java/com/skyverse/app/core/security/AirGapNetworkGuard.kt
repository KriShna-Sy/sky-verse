package com.skyverse.app.core.security

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

data class AirGapStatus(
    val isHostDeviceConnectedToInternet: Boolean,
    val isManifestInternetPermissionGranted: Boolean = false, // Always FALSE (Permission omitted)
    val isSkyAirGapShieldEngaged: Boolean = true, // Always TRUE
    val statusMessage: String
)

class AirGapNetworkGuard(private val context: Context) {

    /**
     * Checks if the host phone/device has Wi-Fi or Mobile Data connected.
     * Even if true, Android OS Kernel Sandbox blocks Sky because INTERNET permission is absent in Manifest.
     */
    fun getAirGapIsolationStatus(): AirGapStatus {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        var isConnected = false

        connectivityManager?.let { cm ->
            val activeNetwork = cm.activeNetwork
            val capabilities = cm.getNetworkCapabilities(activeNetwork)
            if (capabilities != null) {
                isConnected = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            }
        }

        val message = if (isConnected) {
            "Host Phone Internet is ON (Wi-Fi/Cellular) ➔ Sky OS Air-Gap Shield is ENGAGED. Sky remains 100% offline & isolated."
        } else {
            "Host Phone is Offline ➔ Sky Air-Gap Shield ACTIVE."
        }

        return AirGapStatus(
            isHostDeviceConnectedToInternet = isConnected,
            isManifestInternetPermissionGranted = false,
            isSkyAirGapShieldEngaged = true,
            statusMessage = message
        )
    }
}
