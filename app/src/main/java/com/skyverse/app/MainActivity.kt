package com.skyverse.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.skyverse.app.features.benchmark.PerformanceDashboardScreen
import com.skyverse.app.features.home.HomeScreen
import com.skyverse.app.features.memory.MemoryScreen
import com.skyverse.app.features.privacy.PrivacyDashboardScreen
import com.skyverse.app.features.settings.SettingsScreen
import com.skyverse.app.features.skills.SkillsScreen
import com.skyverse.app.features.talk.TalkScreen
import com.skyverse.app.features.voice.VoiceProfileScreen
import com.skyverse.app.ui.theme.CyanHighlight
import com.skyverse.app.ui.theme.DarkSurface
import com.skyverse.app.ui.theme.DeepSpaceBlue
import com.skyverse.app.ui.theme.MidnightNavy
import com.skyverse.app.ui.theme.SkyVerseTheme

sealed class NavScreen(val route: String, val label: String, val icon: ImageVector) {
    object Home : NavScreen("home", "Home", Icons.Default.Home)
    object Talk : NavScreen("talk", "Talk", Icons.Default.Mic)
    object VoiceProfile : NavScreen("voice", "Voice", Icons.Default.Fingerprint)
    object Memory : NavScreen("memory", "Memory", Icons.Default.Psychology)
    object Skills : NavScreen("skills", "Skills", Icons.Default.Build)
    object Privacy : NavScreen("privacy", "Privacy", Icons.Default.Shield)
    object Settings : NavScreen("settings", "Settings", Icons.Default.Settings)
    object Benchmark : NavScreen("benchmark", "Perf", Icons.Default.Speed)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkyVerseTheme {
                MainAppContainer()
            }
        }
    }
}

@Composable
fun MainAppContainer() {
    var currentRoute by remember { mutableStateOf(NavScreen.Home.route) }
    var initialTalkQuery by remember { mutableStateOf<String?>(null) }

    val navItems = listOf(
        NavScreen.Home,
        NavScreen.Talk,
        NavScreen.VoiceProfile,
        NavScreen.Memory,
        NavScreen.Skills,
        NavScreen.Privacy,
        NavScreen.Settings,
        NavScreen.Benchmark
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 8.dp
            ) {
                navItems.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            currentRoute = screen.route
                            if (screen.route != NavScreen.Talk.route) {
                                initialTalkQuery = null
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.label,
                                tint = if (isSelected) CyanHighlight else Color.Gray
                            )
                        },
                        label = {
                            Text(
                                text = screen.label,
                                color = if (isSelected) CyanHighlight else Color.Gray,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = DeepSpaceBlue
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(MidnightNavy)
        ) {
            when (currentRoute) {
                NavScreen.Home.route -> HomeScreen(
                    onNavigateToTalk = { query ->
                        initialTalkQuery = query
                        currentRoute = NavScreen.Talk.route
                    },
                    onNavigateToPrivacy = {
                        currentRoute = NavScreen.Privacy.route
                    }
                )
                NavScreen.Talk.route -> TalkScreen(
                    initialQuery = initialTalkQuery,
                    onBack = { currentRoute = NavScreen.Home.route }
                )
                NavScreen.VoiceProfile.route -> VoiceProfileScreen()
                NavScreen.Memory.route -> MemoryScreen()
                NavScreen.Skills.route -> SkillsScreen()
                NavScreen.Privacy.route -> PrivacyDashboardScreen()
                NavScreen.Settings.route -> SettingsScreen()
                NavScreen.Benchmark.route -> PerformanceDashboardScreen()
            }
        }
    }
}
