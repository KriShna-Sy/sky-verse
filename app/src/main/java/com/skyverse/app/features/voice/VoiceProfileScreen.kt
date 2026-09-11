package com.skyverse.app.features.voice

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VoiceProfileScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Voice Biometric Enrollment", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Please enroll your voice for the ECAPA-TDNN engine to verify your identity.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* TODO: Trigger Coroutine for new VoiceEnrollmentManager */ }) {
            Text("Start Enrollment")
        }
    }
}
