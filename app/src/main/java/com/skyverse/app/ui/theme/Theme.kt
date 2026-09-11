package com.skyverse.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SkyVerseDarkColorScheme = darkColorScheme(
    primary = CyanHighlight,
    onPrimary = MidnightNavy,
    primaryContainer = DeepSpaceBlue,
    onPrimaryContainer = SoftGlowCyan,
    secondary = ElectricBlue,
    onSecondary = Color.White,
    secondaryContainer = RoyalBlue,
    onSecondaryContainer = Color.White,
    tertiary = CelestialGold,
    background = MidnightNavy,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = RoyalBlue
)

@Composable
fun SkyVerseTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SkyVerseDarkColorScheme,
        content = content
    )
}
