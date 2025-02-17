@file:OptIn(ExperimentalFoundationApi::class)

package com.hanpro.prographyproject.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.ui.viewmodel.BookmarksViewModel
import com.hanpro.prographyproject.ui.viewmodel.PhotoViewModel

@Composable
fun HomeScreen(
    photoViewModel: PhotoViewModel = hiltViewModel(),
    bookmarkViewModel: BookmarksViewModel = hiltViewModel(),
    //onPhotoClick: (String) -> Unit,
    ) {
    val uiState by photoViewModel.uiState.collectAsState()
    val bookmarks by bookmarkViewModel.bookmarks.collectAsState()
    val gridState = rememberLazyStaggeredGridState()

    LaunchedEffect(Unit) {
        photoViewModel.loadLatestPhotos(page = 1)
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
                    items(
                        items = uiState.bookmarks,
                        key = { it.id }
                    ) { bookmark ->
                        Card(
                            modifier = Modifier.height(128.dp),
                            // weight 0.3?
                        ) {
                            AsyncImage(
                                model = bookmark.urls.regular,
                                contentDescription = bookmark.description ?: "Photo",
                                // modifier = Modifier.height(128.dp),
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

        items(
            items = uiState.photos,
            key = { it.id }
        ) {
            PhotoItem(photo = it)
        }

        if (uiState.isLoading) {
            item(span = StaggeredGridItemSpan.FullLine) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
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
fun PhotoItem(photo: PhotoDetail) {
    Card(
        modifier = Modifier.fillMaxWidth()
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