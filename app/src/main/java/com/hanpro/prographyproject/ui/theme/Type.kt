package com.hanpro.prographyproject.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hanpro.prographyproject.R

private val Pretendard = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_bold, FontWeight.Bold),
)

val Typography = Typography(
    // 타이틀
    titleLarge = TextStyle(
        fontFamily = Pretendard,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.3).sp
    ),
    // 최신 이미지
    displayMedium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        letterSpacing = (-0.3).sp,
        shadow = Shadow(
            color = Color(0x40000000),
            offset = Offset(0f, 2f),
            blurRadius = 4f
        )
    ),
    // 이미지 설명
    bodyMedium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        letterSpacing = (-0.3).sp
    ),
)
