package com.mehrpol.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MehrpolCyan,
    secondary = MehrpolPrimary,
    tertiary = MehrpolSuccess,
    background = MehrpolDarkBackground,
    surface = MehrpolDarkSurface,
    surfaceVariant = MehrpolSurfaceVariant,
    onPrimary = MehrpolDarkBackground,
    onSecondary = Color.White,
    onTertiary = MehrpolDarkBackground,
    onBackground = MehrpolTextPrimary,
    onSurface = MehrpolTextPrimary,
    onSurfaceVariant = MehrpolTextSecondary,
    error = MehrpolError,
    onError = MehrpolDarkBackground
)

private val LightColorScheme = lightColorScheme(
    primary = MehrpolPrimary,
    secondary = MehrpolCyan,
    tertiary = MehrpolSuccess,
    background = Color(0xFFF7F9FB),
    surface = Color.White,
    surfaceVariant = Color(0xFFE3ECF3),
    onPrimary = Color.White,
    onSecondary = Color(0xFF061419),
    onTertiary = Color(0xFF061419),
    onBackground = Color(0xFF142027),
    onSurface = Color(0xFF142027),
    onSurfaceVariant = Color(0xFF4F6069),
    error = MehrpolError,
    onError = Color.White
)

@Composable
fun MehrpolTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
