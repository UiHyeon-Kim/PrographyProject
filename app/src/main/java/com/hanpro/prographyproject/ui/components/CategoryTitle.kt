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

@Composable
fun CategoryTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier
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