package com.hanpro.prographyproject.ui.dialog

import android.os.Environment
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hanpro.prographyproject.R
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.data.source.remote.downloadImage
import com.hanpro.prographyproject.ui.components.PrographyButtonIcon
import com.hanpro.prographyproject.ui.components.PrographyIconButton
import com.hanpro.prographyproject.ui.components.PrographyNoBackgroundIconButton
import com.hanpro.prographyproject.ui.theme.PrographyProjectTheme
import com.hanpro.prographyproject.ui.viewmodel.PhotoDetailViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun PhotoDetailDialog(
    photoId: String,
    onClose: () -> Unit,
    viewModel: PhotoDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

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

                    // TODO: 함수 분리
                    DetailTopBar(
                        modifier = Modifier.align(Alignment.TopCenter),
                        userName = photo.user.username,
                        isBookmarked = uiState.isBookmarked,
                        onClose = onClose,
                        onDownloadClick = {
                            val fileName = "${photo.id}.jpg"
                            val imageFile = File(
                                context.getExternalFilesDir(Environment.DIRECTORY_DCIM),
                                fileName
                            )
                            val success = downloadImage(photo.links.download, imageFile)
                            if(success) Toast.makeText(context, "이미지를 저장했습니다.", Toast.LENGTH_SHORT).show()
                            else Toast.makeText(context, "이미지를 저장하지 못했습니다.", Toast.LENGTH_SHORT).show()
                        },
                        onBookmarkClick = {
                            if (!uiState.isBookmarked) viewModel.addBookmark(photo)
                            else viewModel.deleteBookmark(photo)
                        }
                    )

                    // TODO 함수 분리
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 10.dp)
                    ) {
                        val description = photo.description ?: ""
                        Text(
                            text = photo.tags?.first()?.title ?: "",
                            style = MaterialTheme.typography.titleLarge,
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

@Preview
@Composable
fun PhotoDetailDialogPreview() {
    PrographyProjectTheme {
        PhotoDetailDialog(
            photoId = "",
            onClose = {},
            viewModel = hiltViewModel()
        )
    }
}
