package com.hanpro.prographyproject.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanpro.prographyproject.R
import com.hanpro.prographyproject.ui.theme.PrographyProjectTheme

@Composable
fun TopBar(modifier: Modifier = Modifier) {
    Column {
        Box(modifier = modifier.fillMaxWidth().padding(top = 10.dp)) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.logo),
                contentDescription = "logo",
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.Center)
                    .height(64.dp)
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    PrographyProjectTheme {
        TopBar()
    }
}