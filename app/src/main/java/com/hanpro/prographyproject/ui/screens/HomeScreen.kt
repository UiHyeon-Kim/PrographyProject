@file:OptIn(ExperimentalFoundationApi::class)

package com.hanpro.prographyproject.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hanpro.prographyproject.ui.components.CategoryTitle
import com.hanpro.prographyproject.ui.components.PhotoCard
import com.hanpro.prographyproject.ui.components.PrographyProgressIndicator
import com.hanpro.prographyproject.ui.dialog.PhotoDetailDialog
import com.hanpro.prographyproject.ui.viewmodel.PhotoViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun HomeScreen(
    viewModel: PhotoViewModel = hiltViewModel(),
) {
    val gridState = rememberLazyStaggeredGridState()
    val uiState by viewModel.uiState.collectAsState()
    var selectedPhotoId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.loadLatestPhotos(page = 1) }

    // 무한 스크롤
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo }
            .map { layoutInfo ->
                val totalItems = layoutInfo.totalItemsCount
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                Pair(totalItems, lastVisibleItemIndex)
            }
            .distinctUntilChanged()
            .filter { (totalItems, lastVisibleItemIndex) ->
                totalItems > 0 && lastVisibleItemIndex >= totalItems - 3
            }
            .collect {
                if (!uiState.isLoading) {
                    val nextPage = (uiState.photos.size / 30) + 1
                    viewModel.loadLatestPhotos(page = nextPage)
                }
            }
    }

    if (uiState.photos.isEmpty()) {
        PrographyProgressIndicator()
    }

    Surface(
        modifier = Modifier.padding(),
        color = MaterialTheme.colorScheme.background
    ) {
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

                if (uiState.photos.isNotEmpty()) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(
                                10.dp,
                                Alignment.Start
                            ),
                            verticalAlignment = Alignment.Top,
                        ) {
                            itemsIndexed(
                                items = uiState.bookmarks,
                                key = { index, bookmark -> "${bookmark.id}_$index" },
                            ) { _, bookmark ->
                                PhotoCard(
                                    cardModifier = Modifier.height(128.dp),
                                    imageUrl = bookmark.imageUrl,
                                    onClick = { selectedPhotoId = bookmark.id },
                                    contentScale = ContentScale.FillHeight
                                )
                            }
                        }
                    }
                } else {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        PrographyProgressIndicator()
                    }
                }
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                CategoryTitle(title = "최신 이미지")
            }

            itemsIndexed(
                uiState.photos,
                key = { index, photo -> "${photo.id}_$index" }) { _, photo ->
                PhotoCard(
                    imageUrl = photo.urls.regular,
                    photoDescription = photo.description,
                    onClick = { selectedPhotoId = photo.id },
                )
            }
        }

        selectedPhotoId?.let { photoId ->
            PhotoDetailDialog(photoId = photoId, onClose = { selectedPhotoId = null })
        }
    }
}