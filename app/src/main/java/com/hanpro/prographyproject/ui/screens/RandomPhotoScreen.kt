package com.hanpro.prographyproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.hanpro.prographyproject.R
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.ui.dialog.PhotoDetailDialog
import com.hanpro.prographyproject.ui.viewmodel.PhotoViewModel

@Composable
fun RandomPhotoScreen(
    viewModel: PhotoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var current by remember { mutableStateOf(0) }
    var selectedPhotoId by remember { mutableStateOf<String?>(null) }

    if (uiState.randomPhotos.isEmpty()) {
        LaunchedEffect(Unit) { viewModel.loadRandomPhotos() }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (current >= uiState.randomPhotos.size) current = 0
    val currentPhoto = uiState.randomPhotos[current]

    // 배경
    Box(
        // TODO: start, end는 좌우 이미지 추가 후 제거하기
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, top = 28.dp, end = 24.dp, bottom = 44.dp),
        contentAlignment = Alignment.Center
    ) {
        // 필름 사진 배경
        Card(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, shape = RoundedCornerShape(15.dp))
                .border(width = 1.dp, color = Color(0xFFEAEBEF), shape = RoundedCornerShape(15.dp)),
            shape = RoundedCornerShape(15.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        ) {
            // 새로 열
            Column(
                modifier = Modifier.background(Color.Transparent)
            ) {
                // 이미지 겹치기 위한 박스
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White)
                ) { RandomPhotoItem(randomPhoto = currentPhoto) }
                // 버튼 행
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(
                            start = 44.dp,
                            top = 24.dp,
                            end = 44.dp,
                            bottom = 24.dp
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // 넘기기? 버튼
                    SideButton(
                        content = "x",
                        iconId = R.drawable.x,
                        onClick = {}
                    )
                    Spacer(modifier = Modifier.width(32.dp))

                    // 북마크 버튼
                    IconButton(
                        modifier = Modifier
                            .width(72.dp)
                            .height(72.dp)
                            .background(
                                color = Color(0xFFD81D45),
                                shape = RoundedCornerShape(36.dp)
                            ),
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.bookmark),
                            contentDescription = "bookmark",
                            tint = Color.White,
                            modifier = Modifier
                                .size(32.dp)
                                .padding(1.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(32.dp))
                    // 디테일 버튼
                    SideButton(
                        content = "information",
                        iconId = R.drawable.information,
                        onClick = { selectedPhotoId = currentPhoto.id }
                    )
                }
            }
        }
    }

    selectedPhotoId?.let { photoId ->
        PhotoDetailDialog(photoId = photoId, onClose = { selectedPhotoId = null })
    }
}

@Composable
fun RandomPhotoItem(randomPhoto: PhotoDetail) {
    // 이미지 틀
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, top = 12.dp, end = 12.dp)
    ) {
        AsyncImage(
            model = randomPhoto.urls.regular,
            contentDescription = randomPhoto.description ?: "Random Photo",
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black, shape = RoundedCornerShape(10.dp)),
        )
    }
}

@Composable
fun SideButton(content: String, iconId: Int, onClick: () -> Unit) {
    IconButton(
        modifier = Modifier
            .width(52.dp)
            .height(52.dp)
            .padding(8.dp)
            .background(color = Color.White, shape = RoundedCornerShape(36.dp))
            .border(1.dp, Color(0xFFEAEBEF), shape = RoundedCornerShape(36.dp)),
        onClick = onClick
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = iconId),
            contentDescription = content,
            tint = Color(0xFFB3B3BE),
            modifier = Modifier
                .size(36.dp)
                .padding(1.dp)
        )
    }
}