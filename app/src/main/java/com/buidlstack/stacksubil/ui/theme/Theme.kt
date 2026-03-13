package com.buidlstack.stacksubil.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = Background,
    primaryContainer = SurfaceVariant,
    onPrimaryContainer = AccentCyan,
    secondary = AccentMint,
    onSecondary = Background,
    secondaryContainer = SurfaceVariant,
    onSecondaryContainer = AccentMint,
    tertiary = AccentPurple,
    onTertiary = Background,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor,
    error = ErrorRed,
    onError = Color.White
)

private val MidnightColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = BackgroundMidnight,
    primaryContainer = SurfaceMidnight,
    onPrimaryContainer = AccentCyan,
    secondary = AccentMint,
    onSecondary = BackgroundMidnight,
    secondaryContainer = SurfaceMidnight,
    onSecondaryContainer = AccentMint,
    tertiary = AccentPurple,
    onTertiary = BackgroundMidnight,
    background = BackgroundMidnight,
    onBackground = TextPrimary,
    surface = SurfaceMidnight,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF0D1830),
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFF1E2D45),
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0C4A6E),
    secondary = Color(0xFF0D9488),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF134E4A),
    tertiary = Color(0xFF7C3AED),
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = DividerColorLight,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun BuildStackTheme(
    appTheme: String = "dark",
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        "midnight" -> MidnightColorScheme
        "light" -> LightColorScheme
        else -> DarkColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = appTheme == "light"
                isAppearanceLightNavigationBars = appTheme == "light"
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
