package com.skyverse.app.features.talk

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyverse.app.core.ai.SkyOrchestrator
import com.skyverse.app.core.ai.SkyResponse
import com.skyverse.app.core.voice.SkyVoicePipeline
import com.skyverse.app.ui.theme.AccentSuccess
import com.skyverse.app.ui.theme.CelestialGold
import com.skyverse.app.ui.theme.CyanHighlight
import com.skyverse.app.ui.theme.DeepSpaceBlue
import com.skyverse.app.ui.theme.ElectricBlue
import com.skyverse.app.ui.theme.MidnightNavy
import com.skyverse.app.ui.theme.RoyalBlue
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "USER" or "SKY"
    val text: String,
    val responseMeta: SkyResponse? = null
)

@Composable
fun TalkScreen(
    initialQuery: String? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val orchestrator = remember { SkyOrchestrator(context) }
    val voicePipeline = remember { SkyVoicePipeline(context) }

    val chatMessages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = "SKY",
                text = "Hello, KriShna. How can I assist you today? All processing is local and private."
            )
        )
    }

    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "orbPulse")
    val orbScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbScale"
    )

    fun handleSend(query: String) {
        if (query.isBlank()) return
        chatMessages.add(ChatMessage("USER", query))
        inputText = ""
        isThinking = true

        scope.launch {
            val skyResponse = orchestrator.processQuery(query)
            chatMessages.add(
                ChatMessage(
                    sender = "SKY",
                    text = skyResponse.text,
                    responseMeta = skyResponse
                )
            )
            isThinking = false
            voicePipeline.speak(skyResponse.spokenText)
        }
    }

    LaunchedEffect(initialQuery) {
        if (!initialQuery.isNullOrBlank()) {
            handleSend(initialQuery)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Sky Companion",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (isThinking) "Sky is thinking..." else "● Listening for \"Hey Sky\"",
                    fontSize = 12.sp,
                    color = if (isThinking) CyanHighlight else AccentSuccess
                )
            }
        }

        // Energy Visualizer Header Ring
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ElectricBlue.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .scale(if (isThinking) orbScale else 1.0f)
                    .size(64.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                CyanHighlight.copy(alpha = 0.8f),
                                RoyalBlue.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }

        // Chat Message List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chatMessages) { msg ->
                if (msg.sender == "USER") {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = RoyalBlue),
                            shape = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text(
                                text = msg.text,
                                fontSize = 14.sp,
                                color = Color.White,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.88f)) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue),
                                shape = RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp),
                                modifier = Modifier.border(
                                    1.dp,
                                    RoyalBlue,
                                    RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp)
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = msg.text,
                                        fontSize = 14.sp,
                                        color = Color.White,
                                        lineHeight = 20.sp
                                    )

                                    msg.responseMeta?.let { meta ->
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (meta.isDeterministic) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            CelestialGold.copy(alpha = 0.15f),
                                                            RoundedCornerShape(12.dp)
                                                        )
                                                        .padding(
                                                            horizontal = 8.dp,
                                                            vertical = 4.dp
                                                        )
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.Bolt,
                                                            contentDescription = null,
                                                            tint = CelestialGold,
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = "DETERMINISTIC (${meta.executionTimeMs}ms)",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = CelestialGold
                                                        )
                                                    }
                                                }
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            CyanHighlight.copy(alpha = 0.15f),
                                                            RoundedCornerShape(12.dp)
                                                        )
                                                        .padding(
                                                            horizontal = 8.dp,
                                                            vertical = 4.dp
                                                        )
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.Psychology,
                                                            contentDescription = null,
                                                            tint = CyanHighlight,
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = "LOCAL LLM (${meta.tokensPerSecond} t/s)",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = CyanHighlight
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Input Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask Sky anything offline...", color = Color.Gray) },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanHighlight,
                    unfocusedBorderColor = RoyalBlue,
                    focusedContainerColor = DeepSpaceBlue,
                    unfocusedContainerColor = DeepSpaceBlue,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { handleSend(inputText) }),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        handleSend(inputText)
                    } else {
                        voicePipeline.startListening()
                        handleSend("What's my battery?")
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(ElectricBlue, CircleShape)
            ) {
                Icon(
                    imageVector = if (inputText.isBlank()) Icons.Default.Mic else Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send / Record",
                    tint = Color.White
                )
            }
        }
    }
}
