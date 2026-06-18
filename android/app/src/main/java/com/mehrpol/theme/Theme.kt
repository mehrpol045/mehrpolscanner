package com.mehrpol.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MehrpolCyan,
    secondary = MehrpolCyan,
    tertiary = MehrpolCyan,
    background = MehrpolDarkBackground,
    surface = MehrpolDarkSurface,
    surfaceVariant = MehrpolSurfaceVariant,
    onPrimary = MehrpolDarkBackground,
    onSecondary = MehrpolDarkBackground,
    onTertiary = MehrpolDarkBackground,
    onBackground = MehrpolTextPrimary,
    onSurface = MehrpolTextPrimary,
    onSurfaceVariant = MehrpolTextSecondary,
    error = MehrpolError,
    onError = MehrpolDarkBackground
)

@Composable
fun MehrpolTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(colorScheme = DarkColorScheme, typography = Typography, content = content)
}
