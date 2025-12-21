package com.example.waterbuddy.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
    darkColorScheme(
        primary = HydrationBlue,
        secondary = HydrationLightBlue,
        tertiary = Pink80,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = HydrationBlue,
        secondary = HydrationLightBlue,
        tertiary = Pink40,
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
