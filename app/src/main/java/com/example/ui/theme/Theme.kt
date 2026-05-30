package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CyberAmber,
    onPrimary = SpaceBlack,
    secondary = NeonGreen,
    onSecondary = SpaceBlack,
    tertiary = MutedSlate,
    background = SpaceBlack,
    onBackground = CleanWhite,
    surface = DeepSlate,
    onSurface = CleanWhite,
    surfaceContainer = CardSlate
)

private val LightColorScheme = lightColorScheme(
    primary = CyberAmber,
    onPrimary = CleanWhite,
    secondary = NeonGreen,
    onSecondary = CleanWhite,
    tertiary = MutedSlate,
    background = CleanWhite,
    onBackground = SpaceBlack,
    surface = CleanWhite,
    onSurface = SpaceBlack,
    surfaceContainer = CardSlate
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep our custom space telemetry colors persistent
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> DarkColorScheme // Force dark theme for the retro key logger cockpit style
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
