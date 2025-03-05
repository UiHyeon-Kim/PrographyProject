@file:OptIn(ExperimentalFoundationApi::class)

package com.hanpro.prographyproject.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.ui.components.BottomNavigation
import com.hanpro.prographyproject.ui.components.TopBar
import com.hanpro.prographyproject.ui.dialog.PhotoDetailDialog
import com.hanpro.prographyproject.ui.viewmodel.PhotoViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun HomeScreen(
    viewModel: PhotoViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyStaggeredGridState()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    var selectedPhotoId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentBackStackEntry) { viewModel.loadLatestPhotos(page = 1) }

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = Color.LightGray,
                strokeWidth = 4.dp,
                progress = 1f
            )

            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = Color.Gray,
                strokeWidth = 4.dp,
            )
        }
    }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavigation(navController) }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
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
                                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                                verticalAlignment = Alignment.Top,
                            ) {
                                itemsIndexed(
                                    items = uiState.bookmarks,
                                    key = { index, bookmark -> "${bookmark.id}_$index" },
                                ) { _, bookmark ->
                                    Card(
                                        modifier = Modifier
                                            .height(128.dp)
                                            .clickable { selectedPhotoId = bookmark.id }) {
                                        AsyncImage(
                                            model = bookmark.imageUrl,
                                            contentDescription = bookmark.description,
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(64.dp),
                                    color = Color.LightGray,
                                    strokeWidth = 4.dp,
                                    progress = 1f
                                )

                                CircularProgressIndicator(
                                    modifier = Modifier.size(64.dp),
                                    color = Color.Gray,
                                    strokeWidth = 4.dp,
                                )
                            }
                        }
                    }
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    CategoryTitle(title = "최신 이미지")
                }

                itemsIndexed(uiState.photos, key = { index, photo -> "${photo.id}_$index" }) { _, photo ->
                    PhotoItem(
                        photo = photo,
                        onClick = { selectedPhotoId = photo.id }
                    )
                }
            }

            // 상태 호이스팅
            selectedPhotoId?.let { photoId ->
                PhotoDetailDialog(photoId = photoId, onClose = { selectedPhotoId = null })
            }
        }
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box {
            AsyncImage(
                model = photo.urls.small,
                contentDescription = photo.description ?: "Photo",
                modifier = Modifier.fillMaxWidth()
            )
            val description = photo.description ?: ""
            if (description.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
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