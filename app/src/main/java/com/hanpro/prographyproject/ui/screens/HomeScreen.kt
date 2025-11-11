@file:OptIn(ExperimentalFoundationApi::class)

package com.hanpro.prographyproject.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

/**
 * 홈 화면 컴포저블로 최신 이미지 목록과 북마크 섹션을 표시하고 사진 상세 다이얼로그를 관리한다.
 *
 * 화면이 처음 구성될 때 최신 사진을 로드하고 상태 표시줄 색상을 투명으로 설정한다. 스크롤 위치에 따라 다음 페이지를 자동으로 로드하는 무한 스크롤을 수행하며,
 * 북마크가 있을 경우 상단에 가로 스크롤 가능한 북마크 목록을, 그 아래에 최신 이미지 격자 레이아웃을 렌더링한다.
 * 사용자가 사진을 선택하면 해당 사진의 상세 다이얼로그를 표시하고, 다이얼로그가 닫히면 선택 상태를 해제한다.
 */
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


    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (uiState.bookmarks.isNotEmpty()) {
                item { CategoryTitle(title = "북마크") }

                if (uiState.photos.isNotEmpty()) {
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(16.dp),
                            verticalAlignment = Alignment.Top,
                            flingBehavior = ScrollableDefaults.flingBehavior(),
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
                    item { PrographyProgressIndicator() }
                }
            }

            item { CategoryTitle(title = "최신 이미지") }
            item {
                LazyVerticalStaggeredGrid(
                    state = gridState,
                    columns = StaggeredGridCells.Fixed(2),
                    verticalItemSpacing = 10.dp,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.heightIn(max = (Short.MAX_VALUE).toInt().dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    userScrollEnabled = false
                ) {
                    itemsIndexed(
                        uiState.photos,
                        key = { index, photo -> "${photo.id}_$index" }) { _, photo ->
                        PhotoCard(
                            cardModifier = Modifier.fillMaxWidth().wrapContentHeight(),
                            imageUrl = photo.urls.regular,
                            photoDescription = photo.description,
                            onClick = { selectedPhotoId = photo.id },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        // 상태 호이스팅
        selectedPhotoId?.let { photoId ->
            PhotoDetailDialog(photoId = photoId, onClose = { selectedPhotoId = null })
        }
    }
}