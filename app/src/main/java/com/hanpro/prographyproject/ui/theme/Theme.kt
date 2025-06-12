package com.hanpro.prographyproject.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BrandColor,
    onPrimary = White,
    secondary = White,
    onSecondary = Gray60,
    tertiaryContainer = Gray20,
    surface = White,
    onSurface = Black,
    surfaceContainer = Black90,
    outline = Gray30,
)

@Composable
fun PrographyProjectTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
