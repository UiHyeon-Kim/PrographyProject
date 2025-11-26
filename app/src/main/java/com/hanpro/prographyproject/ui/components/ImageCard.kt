package com.hanpro.prographyproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/**
 * 이미지를 카드 형태로 표시하고 선택적으로 하단에 캡션을 오버레이로 겹쳐 보여준다.
 *
 * 이미지가 카드 전체를 채우도록 표시하며, 캡션이 제공되면 아래에 투명→반투명 검정 그라디언트 배경 위에 최대 2줄로 텍스트를 렌더링한다.
 *
 * @param imageUrl 표시할 이미지의 URL 또는 이미지 모델.
 * @param contentDescription 접근성(스크린리더)을 위한 이미지 설명. 기본값은 null.
 * @param photoDescription 이미지 하단에 오버레이로 표시할 캡션 텍스트. null 또는 빈 문자열이면 오버레이를 표시하지 않음.
 * @param cardModifier Card에 적용할 Modifier. 클릭 가능 여부에 따라 내부에서 clickable Modifier가 조건부로 합쳐질 수 있음.
 * @param imageModifier AsyncImage에 추가로 적용할 Modifier. 내부적으로 먼저 fillMaxSize()가 적용된 뒤 병합됨.
 * @param shape 카드의 외형(shape).
 * @param backgroundColor 카드 배경색.
 * @param contentScale 이미지의 크기 조정 방식.
 * @param onClick 카드 클릭 시 호출되는 콜백. null이면 클릭 동작이 없음.
 */
@Composable
fun PhotoCard(
    imageUrl: String,
    contentDescription: String? = null,
    photoDescription: String? = null,
    cardModifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    backgroundColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    contentScale: ContentScale = ContentScale.Crop,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = cardModifier
            .clip(shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = shape,
        colors = CardDefaults.cardColors(backgroundColor),
    ) {
        Box {
            AsyncImage(
                modifier = Modifier.fillMaxSize().then(imageModifier),
                model = imageUrl,
                contentDescription = contentDescription,
                contentScale = contentScale,
            )
            val description = photoDescription.orEmpty()
            if (description.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.BottomStart)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.2f)
                                )
                            )
                        )
                ) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White,
                        maxLines = 2,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}