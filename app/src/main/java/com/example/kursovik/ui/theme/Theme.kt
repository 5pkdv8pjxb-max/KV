package com.example.kursovik.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val Black = Color(0xFF000000)

private val KursLightColorScheme = lightColorScheme(
    primary = AccentPrimary,
    onPrimary = Color.White,
    secondary = AccentPrimary,
    onSecondary = Color.White,
    background = BackgroundColor,
    onBackground = TextPrimary,
    surface = SurfaceColor,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceColor,
    onSurfaceVariant = TextSecondary,
    error = ErrorColor,
    onError = Color.White,
    primaryContainer = Color(0xFFE2F5ED),
    onPrimaryContainer = Black,
    secondaryContainer = Color(0xFFE2F5ED),
    onSecondaryContainer = Black,
    surfaceTint = AccentPrimary,
    surfaceContainer = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainerLowest = Color.White,
    surfaceContainerHigh = Color(0xFFF7F7F7),
    surfaceContainerHighest = Color(0xFFF0F0F0),
    outline = DividerColor
)

@Composable
fun KursTheme(content: @Composable () -> Unit) {
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = BackgroundColor.toArgb()
            window.navigationBarColor = BackgroundColor.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = KursLightColorScheme,
        typography = Typography,
        content = content
    )
}