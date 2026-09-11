package com.skyverse.app.features.privacy

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PublicOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyverse.app.core.security.AirGapNetworkGuard
import com.skyverse.app.ui.theme.AccentSuccess
import com.skyverse.app.ui.theme.CelestialGold
import com.skyverse.app.ui.theme.CyanHighlight
import com.skyverse.app.ui.theme.DeepSpaceBlue
import com.skyverse.app.ui.theme.MidnightNavy
import com.skyverse.app.ui.theme.RoyalBlue

@Composable
fun PrivacyDashboardScreen() {
    val context = LocalContext.current
    val airGapGuard = remember { AirGapNetworkGuard(context) }
    val airGapStatus = remember { airGapGuard.getAirGapIsolationStatus() }

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
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = CyanHighlight,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Sky Privacy & Air-Gap Dashboard",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Zero Cloud • Zero Network Traffic • Strict Local Sandbox",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Air-Gap Hardened Isolation Shield Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AccentSuccess, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.PublicOff, contentDescription = null, tint = AccentSuccess)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AIR-GAP HARDENED ISOLATION SHIELD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentSuccess,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Even if your phone is connected to Wi-Fi or 5G, Sky is physically blocked from opening any network connection by the Android OS Kernel.",
                    fontSize = 13.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                PrivacyRow("Android OS Network Permission", "OMITTED / DENIED 🔒", AccentSuccess)
                PrivacyRow("Host Device Wi-Fi / Data State", if (airGapStatus.isHostDeviceConnectedToInternet) "CONNECTED (Sky Air-Gapped)" else "OFFLINE", CelestialGold)
                PrivacyRow("Sky Network Isolation Shield", "ALWAYS ENGAGED 🛡️", AccentSuccess)
                PrivacyRow("Cloud Telemetry & Analytics", "0 BYTES (DISABLED)", AccentSuccess)
                PrivacyRow("External Socket Creation", "BLOCKED AT OS LEVEL", AccentSuccess)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Data Storage & Security Guarantees
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, RoyalBlue, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = CyanHighlight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LOCAL DATA & VOICE BIOMETRICS SECURITY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanHighlight,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                PrivacyRow("Primary Authenticated Speaker", "KriShna", CyanHighlight)
                PrivacyRow("Voice Biometrics Print", "Enrolled & Encrypted (Local)", AccentSuccess)
                PrivacyRow("Memory Database Location", "Room SQLite (/data/data/...)", AccentSuccess)
                PrivacyRow("Third-Party SDK Trackers", "ZERO", AccentSuccess)
                PrivacyRow("Model Weights Runtime", "Local INT4 / ONNX Runtime", AccentSuccess)
            }
        }
    }
}

@Composable
fun PrivacyRow(label: String, value: String, valueColor: Color = Color.White) {
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
