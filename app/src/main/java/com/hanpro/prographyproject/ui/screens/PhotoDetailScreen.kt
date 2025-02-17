package com.hanpro.prographyproject.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.hanpro.prographyproject.ui.viewmodel.BookmarksViewModel
import com.hanpro.prographyproject.ui.viewmodel.PhotoDetailViewModel

@Composable
fun PhotoDetailScreen(
    photoId: String,
    onClose: () -> Unit,
    detailViewModel: PhotoDetailViewModel = hiltViewModel(),
    bookmarksViewModel: BookmarksViewModel = hiltViewModel(),
) {
    val uiState by detailViewModel.uiState.collectAsState()
    val bookmarks by bookmarksViewModel.bookmarks.collectAsState()
    val isBookmarked = bookmarks.any { it.id == photoId }

    LaunchedEffect(photoId) {
        detailViewModel.loadPhotoDetail(photoId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.9f)),
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.error != null) {
            Log.e("PhotoDetailScreen", "Error: ${uiState.error}")
        } else {
            uiState.photo?.let { photo ->
                PhotoDetailContent(photo = photo)

                DetailTopBar(
                    modifier = Modifier.align(Alignment.TopCenter),
                    userName = uiState.photo?.user?.username ?: "",
                    isBookmarked = isBookmarked,
                    onClose = onClose,
                    onDownloadClick = { uiState.photo?.links?.download },
                    onBookmarkClick = {
                        uiState.photo?.let { photo ->
                            // TODO: 북마크 추가 기능 구현
                        }
                    },
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 10.dp)
                ) {
                    Text(
                        //
                        text = "타이틀?",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = photo.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = photo.tags?.joinToString(" ") { it.title } ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        maxLines = 1
                    )
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
            .padding(vertical = 16.dp, horizontal = 12.dp),
//            .height(64.dp),
//        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier
                    .size(36.dp)
//                    .padding(8.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0xFFEAEBEF), shape = RoundedCornerShape(24.dp)),
                onClick = onClose
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.x),
                    contentDescription = "x",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(1.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }

        Row(
//            horizontalArrangement = Arrangement.End,
//            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier
                    .size(40.dp),
//                    .padding(10.dp)
//                    .background(color = Color.Transparent),
                onClick = onDownloadClick,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.download),
                    contentDescription = "download",
                    tint = Color.White,
//                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                modifier = Modifier
                    .size(40.dp),
//                    .padding(10.dp)
//                    .background(color = Color.Transparent),
                onClick = onBookmarkClick,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.bookmark),
                    contentDescription = "bookmark",
                    tint = if (isBookmarked) Color.White else Color(0xFFB3B3BE),
//                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun PhotoDetailContent(photo: PhotoDetail) {
    /*Card(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .background(color = Color.Transparent, shape = RoundedCornerShape(15.dp))
    ) {*/
        AsyncImage(
            model = photo.urls.regular,
            contentDescription = photo.description ?: "Photo",
            modifier = Modifier.fillMaxSize().background(color = Color.Black, shape = RoundedCornerShape(15.dp))
        )
//    }
}
