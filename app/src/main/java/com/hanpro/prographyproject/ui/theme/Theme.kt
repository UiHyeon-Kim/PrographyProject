package com.hanpro.prographyproject.ui.theme

import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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
