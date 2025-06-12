package com.hanpro.prographyproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hanpro.prographyproject.R
import com.hanpro.prographyproject.data.model.PhotoDetail

@Composable
fun PhotoCardItems(
    photo: PhotoDetail,
    onNextClick: () -> Unit,
    onBookmarkClick: (PhotoDetail) -> Unit,
    onDetailClick: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, shape = RoundedCornerShape(15.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(15.dp)
            ),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
    ) {
        Column(modifier = Modifier.background(Color.Transparent)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)
            ) {
                RandomPhotoItem(randomPhoto = photo)
            }
            // 버튼 행
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(start = 44.dp, top = 24.dp, end = 44.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 다음 사진 버튼
                PrographyIconButton(
                    onClick = { onNextClick() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    PrographyButtonIcon(
                        iconId = R.drawable.x,
                        content = "back",
                    )
                }
                Spacer(modifier = Modifier.width(32.dp))

                // 북마크 버튼
                PrographyIconButton(
                    onClick = { onBookmarkClick(photo) },
                    modifier = Modifier.size(72.dp),
                    buttonColor = MaterialTheme.colorScheme.primary,
                    showBorder = false,
                ) {
                    PrographyButtonIcon(
                        iconId = R.drawable.bookmark,
                        content = "bookmark",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))
                // 디테일 버튼
                PrographyIconButton(
                    onClick = { onDetailClick(photo.id) },
                    modifier = Modifier.padding(8.dp)
                ) {
                    PrographyButtonIcon(
                        iconId = R.drawable.information,
                        content = "information",
                    )
                }
            }
        }
    }
}