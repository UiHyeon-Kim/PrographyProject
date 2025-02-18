@file:OptIn(ExperimentalFoundationApi::class)

package com.hanpro.prographyproject.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.ui.dialog.PhotoDetailDialog
import com.hanpro.prographyproject.ui.viewmodel.PhotoViewModel

@Composable
fun HomeScreen(
    viewModel: PhotoViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyStaggeredGridState()
    var selectedPhotoId by remember { mutableStateOf<String?>(null) }

    if (uiState.isLoading) {
        LaunchedEffect(Unit) { viewModel.loadLatestPhotos(page = 1) }
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // TODO: 마지막까지 스크롤 시 사진 추가 로딩 구현

    LazyVerticalStaggeredGrid(
        state = gridState,
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 10.dp,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        if (uiState.bookmarks.isNotEmpty()) {
            item(span = StaggeredGridItemSpan.FullLine) {
                CategoryTitle(title = "북마크")
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp)
                ) {
                    itemsIndexed(
                        items = uiState.bookmarks,
                        key = { index, bookmark -> "${bookmark.id}_$index" }
                    ) { _, bookmark ->
                        Card(modifier = Modifier.height(128.dp)) {
                            AsyncImage(
                                model = bookmark.urls.regular,
                                contentDescription = bookmark.description ?: "Photo",
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            CategoryTitle(title = "최신 이미지")
        }

        // TODO 구현 완료 후 Indexed 제거
        itemsIndexed(uiState.photos, key = { index, photo -> "${photo.id}_$index" }) { _, photo ->
            PhotoItem(
                photo = photo,
                onClick = { selectedPhotoId = photo.id }
            )
        }
    }

    selectedPhotoId?.let { photoId ->
        PhotoDetailDialog(photoId = photoId, onClose = { selectedPhotoId = null })
    }
}

@Composable
fun CategoryTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .padding(start = 4.dp, top = 10.dp, end = 20.dp, bottom = 9.dp)
            .fillMaxWidth()
    )
}

@Composable
fun PhotoItem(
    photo: PhotoDetail,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Box {
            AsyncImage(
                model = photo.urls.regular,
                contentDescription = photo.description ?: "Photo",
                modifier = Modifier.fillMaxWidth()
            )
            val description = photo.description ?: ""
            if (description.isNotEmpty()) {
                Box(
                    modifier = Modifier
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
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        maxLines = 2,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}