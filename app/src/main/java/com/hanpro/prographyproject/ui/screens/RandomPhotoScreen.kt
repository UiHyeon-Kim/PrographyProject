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

/**
 * 랜덤 사진 목록을 페이저로 표시하고 페이저 내비게이션, 북마크 추가 및 사진 상세 보기 상호작용을 처리한다.
 *
 * 초기에 사진 목록이 비어 있으면 데이터를 로드하고 로딩 인디케이터를 표시한다. 페이저의 현재 페이지가 목록 끝에 가까워지면 추가 사진을 사전 로드한다. 각 사진 카드에서 다음 페이지로 이동하거나 북마크를 추가하면 페이저를 다음 페이지로 이동시키고 내부 인덱스를 갱신하며, 사진 상세보기는 다이얼로그로 표시된다.
 */
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