package com.hanpro.prographyproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hanpro.prographyproject.ui.components.PhotoCardItems
import com.hanpro.prographyproject.ui.components.PrographyProgressIndicator
import com.hanpro.prographyproject.ui.dialog.PhotoDetailDialog
import com.hanpro.prographyproject.ui.viewmodel.PhotoViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@Composable
fun RandomPhotoScreen(
    viewModel: PhotoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedPhotoId by remember { mutableStateOf<String?>(null) }
    val pagerState = rememberPagerState(pageCount = { uiState.randomPhotos.size })
    val coroutineScope = rememberCoroutineScope()

    if (uiState.randomPhotos.isEmpty()) {
        LaunchedEffect(Unit) { viewModel.loadRandomPhotos() }
        PrographyProgressIndicator()
        return
    }

    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage }
            .filter { page ->
                uiState.randomPhotos.isNotEmpty() && page >= uiState.randomPhotos.size - 3
            }
            .collect {
                viewModel.loadRandomPhotos()
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 80.dp)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 8.dp,
        ) { page ->
            val photo = uiState.randomPhotos[page]
            PhotoCardItems(
                photo = photo,
                onNextClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(page + 1)
                        viewModel.incrementIndex()
                    }
                },
                onBookmarkClick = {
                    coroutineScope.launch {
                        if (!viewModel.isBookmarked(photo.id)) {
                            viewModel.addBookmark(photo)
                            pagerState.animateScrollToPage(page + 1)
                            viewModel.incrementIndex()
                        }
                    }
                },
                onDetailClick = { selectedPhotoId = it },
            )
        }
    }

    selectedPhotoId?.let { photoId ->
        PhotoDetailDialog(photoId = photoId, onClose = { selectedPhotoId = null })
    }
}