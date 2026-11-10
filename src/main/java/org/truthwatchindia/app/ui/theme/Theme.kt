package org.truthwatchindia.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = CrimsonMaroon,
    onPrimary = CanvasWhite,
    primaryContainer = CrimsonLight,
    onPrimaryContainer = CanvasWhite,
    secondary = DarkCharcoal,
    onSecondary = CanvasWhite,
    background = SurfaceLight,
    onBackground = BodyText,
    surface = CanvasWhite,
    onSurface = DarkCharcoal,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = MetadataGray,
    outline = DividerStroke,
    outlineVariant = DividerStroke
)

private val DarkColorScheme = darkColorScheme(
    primary = CrimsonMaroon,
    onPrimary = CanvasWhite,
    primaryContainer = CrimsonDark,
    onPrimaryContainer = CanvasWhite,
    secondary = DarkTextPrimary,
    onSecondary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkDivider,
    outlineVariant = DarkDivider
)

@Composable
fun TruthWatchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TruthWatchTypography,
        content = content
    )
}
