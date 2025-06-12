package com.hanpro.prographyproject.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanpro.prographyproject.R
import com.hanpro.prographyproject.ui.theme.PrographyProjectTheme
import kotlinx.coroutines.launch

@Composable
fun PrographyTopBar(modifier: Modifier = Modifier) {
    Column {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
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

@Composable
fun DetailTopBar(
    modifier: Modifier = Modifier,
    userName: String = "",
    isBookmarked: Boolean = false,
    onClose: () -> Unit,
    onDownloadClick: suspend () -> Unit,
    onBookmarkClick: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row( // 왼쪽 열
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PrographyIconButton(
                onClick = onClose,
                modifier = Modifier
                    .padding(8.dp)
                    .size(36.dp)
            ) {
                PrographyButtonIcon(
                    iconId = R.drawable.x,
                    content = "close",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }


        Row { // 오른쪽 열
            PrographyNoBackgroundIconButton(
                onClick = { coroutineScope.launch { onDownloadClick() } }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.download),
                    contentDescription = "download",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(4.dp))

            PrographyNoBackgroundIconButton(
                onClick = onBookmarkClick
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.bookmark),
                    contentDescription = "bookmark",
                    tint = if (isBookmarked) Color.White else Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrographyTopBarPreview() {
    PrographyProjectTheme {
        PrographyTopBar()
    }
}

@Preview
@Composable
private fun DetailTopBarPreview() {
    DetailTopBar(
        onClose = {},
        onDownloadClick = {}
    ) {}
}