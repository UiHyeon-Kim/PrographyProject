package com.hanpro.prographyproject.ui.theme

import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

val CustomTypography = Typography(
    // 타이틀
    titleMedium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight(700),
        fontSize = 20.sp
    ),
    // 이미지 타이틀
    titleSmall = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight(500),
        fontSize = 13.sp,
        shadow = Shadow(
            color = Color(0x40000000),
            offset = Offset(0f, 2f),
            blurRadius = 4f
        )
    ),
    // 이미지 설명
    bodyMedium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight(500),
        fontSize = 15.sp,
    ),
)

@Composable
fun PrographyProjectTheme(
    darkTheme: Boolean = false,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CustomTypography,
        content = content
    )
}