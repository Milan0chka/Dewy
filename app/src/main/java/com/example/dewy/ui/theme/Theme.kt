package com.example.dewy.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Pink80,
    primaryContainer = Pink60,
    secondary = Orange40,
    secondaryContainer = Orange20,
    tertiary = Grey40,
    tertiaryContainer = Grey20,
    background = White,
    surface = Pink20,
    surfaceDim = Pink40,


    onPrimary = White,
    onPrimaryContainer = Pink100,
    onSecondary = White,
    onSecondaryContainer = Orange60,
    onTertiary = White,
    onTertiaryContainer = Grey60,
    onBackground = Pink100,
    onSurface = Pink80,
    onSurfaceVariant = Orange40
)

private val LightColorScheme = lightColorScheme(
    primary = Pink80,
    primaryContainer = Pink60,
    secondary = Orange40,
    secondaryContainer = Orange20,
    tertiary = Grey40,
    tertiaryContainer = Grey20,
    background = White,
    surface = Pink20,
    surfaceDim = Pink40,


    onPrimary = White,
    onPrimaryContainer = Pink100,
    onSecondary = White,
    onSecondaryContainer = Orange60,
    onTertiary = White,
    onTertiaryContainer = Grey60,
    onBackground = Pink100,
    onSurface = Pink80,
    onSurfaceVariant = Orange40
)

@Composable
fun DewyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}