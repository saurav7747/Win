package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val EsportsColorScheme = darkColorScheme(
    primary = ElectricBlue,
    secondary = CyberPurple,
    tertiary = EsportsGold,
    background = EsportsBackground,
    surface = EsportsSurface,
    surfaceVariant = EsportsSurfaceVariant,
    onPrimary = Color(0xFF020408),
    onSecondary = Color.White,
    onBackground = TextWhite,
    onSurface = TextWhite,
    onSurfaceVariant = TextGray,
    error = EsportsRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force esports dark theme by default
    content: @Composable () -> Unit
) {
    val colorScheme = EsportsColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = EsportsBackground.toArgb()
            window.navigationBarColor = EsportsBackground.toArgb()
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            // False means white status bar icons (dark background)
            windowInsetsController.isAppearanceLightStatusBars = false
            windowInsetsController.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
