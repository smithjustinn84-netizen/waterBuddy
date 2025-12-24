package com.example.waterbuddy.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
    darkColorScheme(
        primary = MartianRed,
        onPrimary = Color.White,
        primaryContainer = MartianRust,
        onPrimaryContainer = Color.White,
        secondary = GrokkingTeal,
        onSecondary = Color.White,
        secondaryContainer = DeepWaterBlue,
        onSecondaryContainer = Color.White,
        tertiary = MartianSand,
        onTertiary = Color.Black,
        background = MartianDarkSurface,
        onBackground = Color(0xFFE0E0E0),
        surface = MartianDarkSurface,
        onSurface = Color(0xFFE0E0E0),
        surfaceVariant = Color(0xFF2C1E12),
        onSurfaceVariant = Color(0xFFBC8F8F),
    )

private val LightColorScheme =
    lightColorScheme(
        primary = MartianRed,
        onPrimary = Color.White,
        primaryContainer = MartianDust,
        onPrimaryContainer = Color.Black,
        secondary = GrokkingTeal,
        onSecondary = Color.White,
        secondaryContainer = LifeWaterBlue,
        onSecondaryContainer = Color.White,
        tertiary = MartianSand,
        onTertiary = Color.Black,
        background = MartianLightSurface,
        onBackground = Color.Black,
        surface = MartianLightSurface,
        onSurface = Color.Black,
        surfaceVariant = Color(0xFFF5DEB3),
        onSurfaceVariant = Color(0xFF8B4513),
    )

@Composable
fun WaterBuddyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
