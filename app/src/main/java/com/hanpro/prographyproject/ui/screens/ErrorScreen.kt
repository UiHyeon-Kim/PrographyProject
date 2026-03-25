package com.hanpro.prographyproject.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanpro.prographyproject.ui.theme.PrographyProjectTheme

/**
 * 데이터를 불러오는 중 에러가 발생했을 때 사용자에게 안내하고 재시도 동작을 제공하는 UI
 *
 * @param message 표시할 에러 안내 메시지
 * @param onRetry 재시도 버튼을 눌렀을 때 호출되는 콜백
 */
@Composable
fun ErrorScreen(
    message: String = "데이터를 불러오는 중 문제가 발생했습니다.",
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = "에러 발생",
            modifier = Modifier.size(80.dp),
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "오류 발생",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onRetry,
            modifier = Modifier.padding(4.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Text("다시 시도")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    PrographyProjectTheme {
        ErrorScreen(
            message = "데이터를 불러오는 중 문제가 발생했습니다.",
            onRetry = {}
        )
    }
}
