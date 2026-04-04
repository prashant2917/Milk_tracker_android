package com.swarajya.milktracker.common.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = MilkBlue,
    onPrimary = Color.White,
    secondary = MilkGreen,
    onSecondary = Color.White,
    background = MilkWhite,
    onBackground = MilkTextPrimary,
    surface = MilkSurface,
    onSurface = MilkTextPrimary,
    outline = MilkOutline,
    error = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = MilkBlueDark,
    onPrimary = Color.White,
    secondary = MilkGreenDark,
    onSecondary = Color.White,
    background = MilkDarkBackground,
    onBackground = MilkDarkTextPrimary,
    surface = MilkDarkSurface,
    onSurface = MilkDarkTextPrimary,
    outline = MilkDarkOutline,
    error = ErrorRed
)

@Composable
fun MilkTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        window.statusBarColor = colorScheme.primary.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MilkTypography,
        shapes = MilkShapes,
        content = content
    )
}
