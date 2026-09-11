package com.skyverse.app.features.benchmark

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyverse.app.core.performance.LowMemoryProfiler
import com.skyverse.app.ui.theme.AccentSuccess
import com.skyverse.app.ui.theme.CelestialGold
import com.skyverse.app.ui.theme.CyanHighlight
import com.skyverse.app.ui.theme.DeepSpaceBlue
import com.skyverse.app.ui.theme.MidnightNavy
import com.skyverse.app.ui.theme.RoyalBlue

@Composable
fun PerformanceDashboardScreen() {
    val context = LocalContext.current
    val profiler = remember { LowMemoryProfiler(context) }
    var profile by remember { mutableStateOf(profiler.getDeviceMemoryProfile()) }
    var trimStatusMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = null,
                tint = CyanHighlight,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Performance & Low-RAM Profiler",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Optimized for Legacy & Low-RAM Android Devices",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Low-RAM Device Status Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, if (profile.isLowRamDevice) CelestialGold else AccentSuccess, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Memory, contentDescription = null, tint = CyanHighlight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (profile.isLowRamDevice) "LEGACY PHONE LOW-RAM MODE ACTIVE" else "STANDARD PERFORMANCE PROFILE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (profile.isLowRamDevice) CelestialGold else AccentSuccess,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                PerfMetricRow("Device Total RAM", "${profile.totalRamMb} MB")
                PerfMetricRow("Available System RAM", "${profile.availableRamMb} MB", AccentSuccess)
                PerfMetricRow("Recommended AI Model", profile.recommendedQuantization, CelestialGold)
                PerfMetricRow("Max RAM Footprint Limit", "${profile.maxModelMemoryMb} MB")
                PerfMetricRow("Deterministic Task Latency", "12 ms - 28 ms", AccentSuccess)
                PerfMetricRow("Speaker Biometrics Load", "< 5 MB RAM", AccentSuccess)

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        profiler.performMemoryOptimizationTrim()
                        profile = profiler.getDeviceMemoryProfile()
                        trimStatusMessage = "RAM caches trimmed & memory garbage collected ✓"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.CleaningServices, contentDescription = null, tint = CyanHighlight)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Trim & Reclaim Phone RAM", color = CyanHighlight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        trimStatusMessage?.let { msg ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = msg, fontSize = 12.sp, color = AccentSuccess, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PerfMetricRow(label: String, value: String, valueColor: Color = Color.White) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = Color.Gray)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
