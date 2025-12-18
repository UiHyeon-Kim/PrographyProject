package com.hanpro.prographyproject.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanpro.prographyproject.ui.theme.PrographyProjectTheme

/**
 * 카테고리 제목을 화면에 표시한다.
 *
 * 주어진 문자열을 MaterialTheme.typography.titleLarge 스타일로 렌더링하며, 좌우 20dp, 상단 10dp, 하단 9dp의 패딩을 적용하고 가로 전체를 채운다.
 *
 * @param title 표시할 제목 문자열
 */
@Composable
fun CategoryTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier
            .padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 9.dp)
            .fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
private fun CategoryTitlePreview() {
    PrographyProjectTheme {
        CategoryTitle("preview")
    }
}