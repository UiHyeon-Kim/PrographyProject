package com.hanpro.prographyproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanpro.prographyproject.ui.theme.PrographyProjectTheme

@Composable
fun PrographyIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBorder: Boolean = true,
    buttonColor: Color = MaterialTheme.colorScheme.secondary,
    icon: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(52.dp)
            .background(color = buttonColor, shape = CircleShape)
            .then(
                if (showBorder) Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                ) else Modifier
            )
    ) {
        icon()
    }
}

@Composable
fun PrographyNoBackgroundIconButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    IconButton(onClick = onClick) { icon() }
}

@Composable
fun PrographyButtonIcon(
    iconId: Int,
    content: String,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSecondary,
) {
    Icon(
        imageVector = ImageVector.vectorResource(id = iconId),
        contentDescription = content,
        tint = tint,
        modifier = modifier
            .size(36.dp)
            .padding(1.dp)
    )
}

@Preview
@Composable
fun PrographyButtonPreview() {
    PrographyProjectTheme {
        PrographyIconButton(
            onClick = {},
            modifier = Modifier,
            icon = {}
        )
    }
}