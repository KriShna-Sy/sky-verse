package com.skyverse.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
import dagger.hilt.android.AndroidEntryPoint

sealed class NavScreen(val route: String, val label: String, val icon: ImageVector) {
    object Home : NavScreen("home", "Home", Icons.Default.Home)
    object Talk : NavScreen("talk", "Talk", Icons.Default.Mic)
    object Memory : NavScreen("memory", "Memory", Icons.Default.Psychology)
    object More : NavScreen("more", "More", Icons.Default.Menu)
    
    // Sub-screens
    object VoiceProfile : NavScreen("voice", "Voice", Icons.Default.Menu)
    object Skills : NavScreen("skills", "Skills", Icons.Default.Menu)
    object Privacy : NavScreen("privacy", "Privacy", Icons.Default.Menu)
    object Settings : NavScreen("settings", "Settings", Icons.Default.Menu)
    object Benchmark : NavScreen("benchmark", "Perf", Icons.Default.Menu)
}

@AndroidEntryPoint
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
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavScreen.Home.route

    val bottomNavItems = listOf(
        NavScreen.Home,
        NavScreen.Talk,
        NavScreen.Memory,
        NavScreen.More
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 8.dp
            ) {
                bottomNavItems.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                navController.navigate(screen.route) {
                                    popUpTo(NavScreen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
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
            NavHost(navController = navController, startDestination = NavScreen.Home.route) {
                composable(NavScreen.Home.route) {
                    HomeScreen(
                        onNavigateToTalk = { query ->
                            navController.currentBackStackEntry?.savedStateHandle?.set("initialQuery", query)
                            navController.navigate(NavScreen.Talk.route)
                        },
                        onNavigateToPrivacy = {
                            navController.navigate(NavScreen.Privacy.route)
                        }
                    )
                }
                composable(NavScreen.Talk.route) { backStackEntry ->
                    val initialQuery = backStackEntry.savedStateHandle.get<String>("initialQuery")
                    TalkScreen(
                        initialQuery = initialQuery,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(NavScreen.Memory.route) {
                    MemoryScreen()
                }
                composable(NavScreen.More.route) {
                    SettingsScreen()
                }
                
                // Sub-screens
                composable(NavScreen.VoiceProfile.route) { VoiceProfileScreen() }
                composable(NavScreen.Skills.route) { SkillsScreen() }
                composable(NavScreen.Privacy.route) { PrivacyDashboardScreen() }
                composable(NavScreen.Benchmark.route) { PerformanceDashboardScreen() }
            }
        }
    }
}
