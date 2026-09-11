package com.skyverse.app.features.voice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.skyverse.app.core.ai.SkyOrchestrator
import com.skyverse.app.core.voice.EnrollmentStep
import com.skyverse.app.core.voice.VoiceEnrollmentManager
import com.skyverse.app.ui.theme.AccentDanger
import com.skyverse.app.ui.theme.AccentSuccess
import com.skyverse.app.ui.theme.CelestialGold
import com.skyverse.app.ui.theme.CyanHighlight
import com.skyverse.app.ui.theme.DeepSpaceBlue
import com.skyverse.app.ui.theme.ElectricBlue
import com.skyverse.app.ui.theme.MidnightNavy
import com.skyverse.app.ui.theme.RoyalBlue
import kotlinx.coroutines.launch

@Composable
fun VoiceProfileScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val orchestrator = remember { SkyOrchestrator(context) }
    val enrollmentManager = remember { VoiceEnrollmentManager(orchestrator.speakerVerificationEngine) }
    val enrollmentState by enrollmentManager.enrollmentState.collectAsState()

    var isSpeakerLockOn by remember { mutableStateOf(orchestrator.speakerVerificationEngine.isSpeakerLockEnabled()) }
    var testResultText by remember { mutableStateOf<String?>(null) }
    var lastConfidence by remember { mutableStateOf<Float?>(null) }

    fun runSpeakerTest(asKriShna: Boolean) {
        scope.launch {
            val response = orchestrator.processQuery("Sky, what's my battery level?", simulateSpeakerMatch = asKriShna)
            testResultText = response.text
            lastConfidence = response.verificationResult?.matchConfidence
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Title Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.RecordVoiceOver,
                contentDescription = null,
                tint = CyanHighlight,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Speaker Voice Customization",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Sky responds strictly to KriShna's authenticated voice",
                    fontSize = 12.sp,
                    color = AccentSuccess
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Status Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, RoyalBlue, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, tint = CyanHighlight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VOICE BIOMETRIC LOCK",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanHighlight,
                            letterSpacing = 1.sp
                        )
                    }

                    Switch(
                        checked = isSpeakerLockOn,
                        onCheckedChange = {
                            isSpeakerLockOn = it
                            orchestrator.speakerVerificationEngine.setSpeakerLockEnabled(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MidnightNavy,
                            checkedTrackColor = CyanHighlight
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Enrolled Primary Speaker:", fontSize = 14.sp, color = Color.White)
                    Text(text = "KriShna ✓", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AccentSuccess)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Biometric Match Threshold:", fontSize = 14.sp, color = Color.White)
                    Text(text = "78.0% Cosine Similarity", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CelestialGold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Enrollment Wizard Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, RoyalBlue, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "VOICE PROFILE ENROLLMENT WIZARD",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanHighlight,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = enrollmentState.promptPhrase,
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val currentStepInt = when (enrollmentState.step) {
                        EnrollmentStep.SAMPLE_1 -> 1
                        EnrollmentStep.SAMPLE_2 -> 2
                        EnrollmentStep.SAMPLE_3 -> 3
                        EnrollmentStep.COMPLETED -> 3
                        else -> 0
                    }

                    for (i in 1..3) {
                        val isDone = currentStepInt >= i
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .background(
                                    color = if (isDone) CyanHighlight else RoyalBlue.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (enrollmentState.step == EnrollmentStep.NOT_STARTED || enrollmentState.isComplete) {
                        Button(
                            onClick = { enrollmentManager.startEnrollment() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanHighlight),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = MidnightNavy)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Record Voice Profile", color = MidnightNavy, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { enrollmentManager.recordSample() },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Record Sample ${enrollmentState.samplesRecorded + 1}/3", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Speaker Authentication Live Test Bench
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, RoyalBlue, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SPEAKER AUTHENTICATION TEST BENCH",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanHighlight,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Test how Sky reacts when KriShna speaks vs an unrecognized voice.",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { runSpeakerTest(asKriShna = true) },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentSuccess.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, AccentSuccess, RoundedCornerShape(12.dp))
                    ) {
                        Text("🗣️ Speak as KriShna", fontSize = 12.sp, color = AccentSuccess, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { runSpeakerTest(asKriShna = false) },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentDanger.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, AccentDanger, RoundedCornerShape(12.dp))
                    ) {
                        Text("👤 Unknown Voice", fontSize = 12.sp, color = AccentDanger, fontWeight = FontWeight.Bold)
                    }
                }

                testResultText?.let { result ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MidnightNavy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "TEST OUTPUT:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanHighlight)
                                lastConfidence?.let { conf ->
                                    Text(
                                        text = "Match: ${(conf * 100).toInt()}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (conf >= 0.78f) AccentSuccess else AccentDanger
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = result, fontSize = 13.sp, color = Color.White, lineHeight = 18.sp)
                        }
                    }
                }
            }
        }
    }
}
