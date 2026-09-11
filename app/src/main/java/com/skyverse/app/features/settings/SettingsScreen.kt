package com.skyverse.app.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyverse.app.core.ai.LocalModelManager
import com.skyverse.app.core.memory.MemoryManager
import kotlinx.coroutines.launch
import com.skyverse.app.ui.theme.AccentSuccess
import com.skyverse.app.ui.theme.CyanHighlight
import com.skyverse.app.ui.theme.DeepSpaceBlue
import com.skyverse.app.ui.theme.MidnightNavy
import com.skyverse.app.ui.theme.RoyalBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val memoryManager = remember { MemoryManager(context) }
    val modelManager = remember { LocalModelManager() }
    val installedModels = remember { modelManager.getInstalledModels() }

    var userName by remember { mutableStateOf("KriShna") }
    var selectedProactiveIndex by remember { mutableIntStateOf(1) } // 0=OFF, 1=LOW, 2=BALANCED, 3=HIGH
    val proactiveOptions = listOf("OFF", "LOW", "BALANCED", "HIGH")

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
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = CyanHighlight,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Sky Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Personalization & Intelligence Controls",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Personalization Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, RoyalBlue, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "PERSONALIZATION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanHighlight,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = userName,
                    onValueChange = { newName ->
                        userName = newName
                        scope.launch {
                            memoryManager.saveMemory("User Preferred Name", newName, "PREFERENCE")
                        }
                    },
                    label = { Text("Preferred User Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanHighlight,
                        unfocusedBorderColor = RoyalBlue,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Proactive Intelligence Setting Card (Spec Section 16)
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, RoyalBlue, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = CyanHighlight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PROACTIVE ASSISTANCE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanHighlight,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Sky will suggest contextually useful actions (e.g. low battery & early alarm) without interrupting.",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    proactiveOptions.forEachIndexed { index, label ->
                        val isSelected = selectedProactiveIndex == index
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = if (isSelected) CyanHighlight else RoyalBlue.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedProactiveIndex = index }
                                .padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MidnightNavy else Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Model Manager Registry (Spec Section 26)
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, RoyalBlue, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Memory, contentDescription = null, tint = CyanHighlight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LOCAL MODEL REGISTRY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanHighlight,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                installedModels.forEach { model ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = model.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = "STATUS: ${model.status}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentSuccess)
                        }
                        Text(text = "${model.quantization} • ${model.sizeMb}MB RAM", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
