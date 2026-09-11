package com.skyverse.app.core.voice

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Foreground service that holds the microphone and runs the always-listening loop.
 * It manages WakeWordEngine and SileroVadEngine.
 */
class SkyListeningService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // TODO: Create Notification for Foreground Service
        // TODO: Start AudioRecord loop
        // TODO: Feed audio to WakeWordEngine
        // TODO: On "Hey Sky", trigger SileroVAD to record query, then pass to WhisperEngine
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // TODO: Cleanup AudioRecord
    }
}
