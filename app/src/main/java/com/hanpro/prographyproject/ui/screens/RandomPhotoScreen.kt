package com.hanpro.prographyproject.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RandomPhotoScreen() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(top = 28.dp, bottom = 44.dp)
    ) {
        Column {
            // TODO: Unsplash 이미지
            Row(
                horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 44.dp, top = 24.dp, end = 43.dp,bottom = 24.dp)
            ) {
                // TODO: 버튼 UI
            }
        }
    }
}