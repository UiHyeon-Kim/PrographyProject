package com.hanpro.prographyproject.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hanpro.prographyproject.ui.theme.PrographyProjectTheme

@Composable
fun PrographyProgressIndicator(
    modifier: Modifier = Modifier.fillMaxSize(),
    contentAlignment: Alignment = Alignment.Center,
    indicatorSize: Dp = 64.dp,
    indicatorWidth: Dp = 4.dp,
    baseColor: Color = Color.LightGray,
    highlightsColor: Color = Color.Gray,
) {
    Box(
        modifier = modifier,
        contentAlignment = contentAlignment
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(indicatorSize),
            strokeWidth = indicatorWidth,
            color = baseColor,
            progress = { 1f }
        )

        CircularProgressIndicator(
            modifier = Modifier.size(indicatorSize),
            strokeWidth = indicatorWidth,
            color = highlightsColor,
        )
    }
}

@Preview
@Composable
private fun IndicatorPreView() {
    PrographyProjectTheme {
        PrographyProgressIndicator()
    }
}