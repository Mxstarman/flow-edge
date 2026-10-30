package com.ecrisar.flow.edge.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = OneUIBlue,
    onPrimary = Color.White,
    primaryContainer = OneUIDarkCard,
    onPrimaryContainer = OneUITextPrimary,
    surface = OneUIDarkSurface,
    onSurface = OneUITextPrimary,
    surfaceVariant = OneUIDarkCardSecondary,
    onSurfaceVariant = OneUITextSecondary,
    background = Color(0xFF0F1012),
    onBackground = OneUITextPrimary
)

@Composable
fun EdgePanelTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
