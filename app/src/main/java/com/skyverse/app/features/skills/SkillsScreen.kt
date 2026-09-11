package com.skyverse.app.features.skills

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.TouchApp
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
import com.skyverse.app.tools.ToolManager
import com.skyverse.app.ui.theme.AccentSuccess
import com.skyverse.app.ui.theme.CelestialGold
import com.skyverse.app.ui.theme.CyanHighlight
import com.skyverse.app.ui.theme.DeepSpaceBlue
import com.skyverse.app.ui.theme.MidnightNavy
import com.skyverse.app.ui.theme.RoyalBlue

@Composable
fun SkillsScreen() {
    val context = LocalContext.current
    val toolManager = remember { ToolManager(context) }
    var selectedToolOutput by remember { mutableStateOf<String?>(null) }
    val isAutomationActive = remember { toolManager.uiAutomationTool.isAutomationEnabled() }

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
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = CyanHighlight,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Sky Tools & Device Intelligence",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "PDF Tracer • Person Photo Search • Ad-Skipper",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Accessibility UI Automation Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, if (isAutomationActive) AccentSuccess else CelestialGold, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = CyanHighlight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AUTOMATED UI & AD-SKIPPER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanHighlight,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = if (isAutomationActive) "ACTIVE ✓" else "DISABLED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAutomationActive) AccentSuccess else CelestialGold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Sky uses Android Accessibility Service to automatically click 'Skip Ad', press 'X' close buttons, and execute tap/swipe gestures inside external apps on behalf of KriShna.",
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { toolManager.uiAutomationTool.openAccessibilitySettings() },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanHighlight),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.TouchApp, contentDescription = null, tint = MidnightNavy)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAutomationActive) "Accessibility Service Enabled ✓" else "Enable Accessibility Service in Settings",
                        color = MidnightNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tool List including File Tracer & Character Photo Search
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, RoyalBlue, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "OFFLINE INTELLIGENCE & FILE TRACER",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanHighlight,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                SkillRow("📄 Trace PDF & Local Files", "Search storage for PDFs by name") {
                    selectedToolOutput = toolManager.traceFileByName("pdf")
                }

                SkillRow("🖼️ Person Character Photo Search", "Search gallery photos tagged for KriShna") {
                    selectedToolOutput = toolManager.searchPhotosByPerson("KriShna")
                }

                SkillRow("⏩ Auto-Skip Ads & Close Popups", "Triggers Accessibility Ad Scanner") {
                    selectedToolOutput = toolManager.autoSkipAds()
                }

                SkillRow("🚀 Launch YouTube & Auto-Skip", "Launches App & Starts UI Automation") {
                    val launchRes = toolManager.launchApp("YouTube")
                    val skipRes = toolManager.autoSkipAds()
                    selectedToolOutput = "$launchRes\n$skipRes"
                }

                SkillRow("🔋 Battery Status", "Queries BatteryManager directly") {
                    selectedToolOutput = toolManager.getBatterySummary()
                }

                SkillRow("💡 Toggle Flashlight", "CameraManager LED Control") {
                    selectedToolOutput = toolManager.toggleFlashlight(true)
                }

                SkillRow("📱 Device Metrics", "Queries RAM & Active CPU Cores") {
                    selectedToolOutput = toolManager.getDeviceMetricsSummary()
                }
            }
        }

        selectedToolOutput?.let { output ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyanHighlight, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TOOL OUTPUT RESULT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanHighlight
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = output, fontSize = 13.sp, color = Color.White, lineHeight = 18.sp)
                }
            }
        }
    }
}

@Composable
fun SkillRow(title: String, description: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = description, fontSize = 11.sp, color = Color.Gray)
        }
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "EXECUTE", fontSize = 10.sp, color = CyanHighlight, fontWeight = FontWeight.Bold)
        }
    }
}
