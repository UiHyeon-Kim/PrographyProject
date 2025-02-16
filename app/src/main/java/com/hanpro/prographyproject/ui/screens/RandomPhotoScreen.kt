package com.hanpro.prographyproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanpro.prographyproject.R
import com.hanpro.prographyproject.data.model.PhotoDetail

@Composable
fun RandomPhotoScreen(
    photos: List<PhotoDetail>,
    onBookmarkAdd: (PhotoDetail) -> Unit,
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 이미지 틀
        Card(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .border(width = 1.dp, color = Color(0xFFEAEBEF), shape = RoundedCornerShape(15.dp))
                .shadow(
                    elevation = 25.dp,
                    spotColor = Color(0x1F000000),
                    ambientColor = Color(0x1F000000)
                )
                .padding(start = 24.dp, top = 28.dp, end = 24.dp, bottom = 44.dp),
            shape = RoundedCornerShape(15.dp)
        ) {
            Column {
                // 이미지
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 0.dp)
                        .background(color = Color(0xFF070707)),
                    shape = RoundedCornerShape(10.dp)
                ) { }
                // 버튼 행
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 44.dp,
                            top = 24.dp,
                            end = 44.dp,
                            bottom = 24.dp
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        modifier = Modifier
                            .width(52.dp)
                            .height(52.dp)
                            .padding(8.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(36.dp))
                            .border(1.dp, Color(0xFFEAEBEF), shape = RoundedCornerShape(36.dp)),
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.x),
                            contentDescription = "x",
                            tint = Color(0xFFB3B3BE),
                            modifier = Modifier.size(36.dp).padding(1.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(32.dp))

                    IconButton(
                        modifier = Modifier
                            .width(72.dp)
                            .height(72.dp)
                            .background(color = Color(0xFFD81D45), shape = RoundedCornerShape(36.dp)),
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.bookmark),
                            contentDescription = "bookmark",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp).padding(1.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(32.dp))

                    IconButton(
                        modifier = Modifier
                            .width(52.dp)
                            .height(52.dp)
                            .padding(8.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(36.dp))
                            .border(1.dp, Color(0xFFEAEBEF), shape = RoundedCornerShape(36.dp)),
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.information),
                            contentDescription = "information",
                            tint = Color(0xFFB3B3BE),
                            modifier = Modifier.size(36.dp).padding(1.dp)
                        )
                    }
                }
            }

        }
    }


}

@Preview(showBackground = true)
@Composable
fun Preview() {
    RandomPhotoScreen(photos = emptyList(), onBookmarkAdd = {})
}