package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = KidPurpleLight,
    onPrimary = Color.Black,
    primaryContainer = KidSurfaceDark,
    onPrimaryContainer = KidPurpleLight,
    secondary = KidPink,
    onSecondary = Color.White,
    tertiary = KidYellow,
    background = KidBackgroundDark,
    onBackground = TextPrimaryDark,
    surface = KidSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF382654),
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFF5E438A),
    error = KidRed
)

private val LightColorScheme = lightColorScheme(
    primary = KidPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE7F6),
    onPrimaryContainer = KidPurple,
    secondary = KidOrange,
    onSecondary = Color.White,
    tertiary = KidYellow,
    background = KidBackgroundLight,
    onBackground = TextPrimaryLight,
    surface = KidSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF3E5F5),
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFD1C4E9),
    error = KidRed
)

@Composable
fun KidsLearningTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
