package com.hanpro.prographyproject.ui.dialog

import android.util.Log
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hanpro.prographyproject.R
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.ui.viewmodel.PhotoDetailViewModel

@Composable
fun PhotoDetailDialog(
    photoId: String,
    onClose: () -> Unit,
    viewModel: PhotoDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val systemUiController = rememberSystemUiController()

    LaunchedEffect(photoId) {
        viewModel.loadPhotoDetail(photoId)
    }

    DisposableEffect(Unit) {
        systemUiController.setStatusBarColor(Color.Black.copy(alpha = 0.7f), darkIcons = false)
        onDispose { systemUiController.setStatusBarColor(color = Color(0x00000000), darkIcons = true) }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.error != null) {
                Log.e("PhotoDetailScreen", "Error: ${uiState.error}")
            } else {
                uiState.photo?.let { photo ->
                    // 디테일 이미지
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .align(Alignment.Center)
                    ) { PhotoDetailContent(photo = photo) }

                    DetailTopBar(
                        modifier = Modifier.align(Alignment.TopCenter),
                        userName = photo.user.username,
                        isBookmarked = uiState.isBookmarked,
                        onClose = onClose,
                        onDownloadClick = { photo.links.download },
                        onBookmarkClick = { /* TODO: 북마크 추가 기능 구현 */ }
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 10.dp)
                    ) {
                        val description = photo.description ?: ""
                        Text(
                            text = photo.tags?.first()?.title ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        if (description.isNotEmpty()) {
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                maxLines = 2
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = photo.tags?.joinToString(" ") { "#${it.title}" } ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailTopBar(
    modifier: Modifier,
    userName: String = "",
    isBookmarked: Boolean,
    onClose: () -> Unit,
    onDownloadClick: () -> Unit,
    onBookmarkClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 왼쪽 열
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 닫기 버튼
            IconButton(
                modifier = Modifier
//                    .size(24.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0xFFEAEBEF), shape = RoundedCornerShape(24.dp)),
                onClick = onClose
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.x),
                    contentDescription = "close",
                    tint = Color.Black,
                    modifier = Modifier.padding(1.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }

        // 오른쪽 열
        Row {
            // 다운로드 버튼
            IconButton(
                modifier = Modifier.size(40.dp),
                onClick = onDownloadClick,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.download),
                    contentDescription = "download",
                    tint = Color.White,
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // 북마크 버튼
            IconButton(
                modifier = Modifier.size(40.dp),
                onClick = onBookmarkClick,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.bookmark),
                    contentDescription = "bookmark",
                    tint = if (isBookmarked) Color.White else Color.Gray,
                )
            }
        }
    }
}

@Composable
fun PhotoDetailContent(photo: PhotoDetail) {
    Card(
        Modifier
            .fillMaxWidth()
            .background(color = Color.Transparent, shape = RoundedCornerShape(15.dp))
    ) {
        AsyncImage(
            model = photo.urls.regular,
            contentDescription = photo.description ?: "Photo",
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Transparent, shape = RoundedCornerShape(15.dp))
        )
    }
}
