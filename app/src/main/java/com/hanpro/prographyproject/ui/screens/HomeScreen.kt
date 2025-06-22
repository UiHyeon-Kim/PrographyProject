@file:OptIn(ExperimentalFoundationApi::class)

package com.hanpro.prographyproject.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
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
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit) {
        viewModel.loadLatestPhotos(page = 1)
        systemUiController.setStatusBarColor(color = Color(0x00000000), darkIcons = true)
    }

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

    Surface(color = MaterialTheme.colorScheme.background) {
        LazyColumn(Modifier.fillMaxSize().padding(top = 20.dp)) {
            val bookmarks = uiState.bookmarks
            if (bookmarks.isNotEmpty()) {
                item { CategoryTitle("북마크") }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        items(
                            items = bookmarks,
                            key = { it.id }
                        ) { bookmark ->
                            PhotoCard(
                                cardModifier = Modifier.height(128.dp),
                                imageUrl = bookmark.imageUrl,
                                onClick = { selectedPhotoId = bookmark.id },
                                contentScale = ContentScale.FillHeight
                            )
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(height = 12.dp)) }
            item { CategoryTitle(title = "최신 이미지") }
            item {
                LazyVerticalStaggeredGrid(
                    modifier = Modifier.heightIn(max = (Short.MAX_VALUE).toInt().dp).padding(top = 12.dp),
                    state = gridState,
                    columns = StaggeredGridCells.Fixed(2),
                    verticalItemSpacing = 10.dp,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                ) {
                    items(
                        items = uiState.photos,
                        key = { it.id },
                    ) { photo ->
                        PhotoCard(
                            imageUrl = photo.urls.regular,
                            photoDescription = photo.description,
                            onClick = { selectedPhotoId = photo.id },
                        )
                    }
                }
            }
        }
        selectedPhotoId?.let { photoId ->
            PhotoDetailDialog(photoId = photoId, onClose = { selectedPhotoId = null })
        }
    }
}