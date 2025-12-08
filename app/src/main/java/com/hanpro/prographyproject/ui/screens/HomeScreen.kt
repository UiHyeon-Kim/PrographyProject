@file:OptIn(ExperimentalFoundationApi::class)

package com.hanpro.prographyproject.ui.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hanpro.prographyproject.common.utils.NetworkEvent
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.data.source.local.Bookmark
import com.hanpro.prographyproject.ui.components.CategoryTitle
import com.hanpro.prographyproject.ui.components.PhotoCard
import com.hanpro.prographyproject.ui.components.PrographyProgressIndicator
import com.hanpro.prographyproject.ui.dialog.PhotoDetailDialog
import com.hanpro.prographyproject.ui.viewmodel.PhotoViewModel
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.flow.distinctUntilChanged

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
    val uiState by viewModel.uiState.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val networkEvent by viewModel.networkEvent.collectAsState()

    val context = LocalContext.current
    val gridState = rememberLazyStaggeredGridState()
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit) {
        systemUiController.setStatusBarColor(color = Color(0x00000000), darkIcons = true)
    }

    LaunchedEffect(networkEvent) {
        when (networkEvent) {
            is NetworkEvent.Connected -> {
                viewModel.retryConnection()
            }

            is NetworkEvent.Disconnected -> {
                Toast.makeText(context, "네트워크 연결이 끊어졌습니다", Toast.LENGTH_SHORT).show()
                viewModel.onNetworkEventShown()
            }

            null -> {
                // 초기 상태, 처리 불필요
            }
        }
    }

    // 무한 스크롤
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex == null) return@collect

                val totalItems = gridState.layoutInfo.totalItemsCount

                if (lastVisibleIndex >= totalItems - 3 && !uiState.isLoading && isConnected) {
                    val nextPage = uiState.currentPage + 1
                    viewModel.loadLatestPhotos(page = nextPage)
                }
            }
    }

    when {
        !isConnected -> {
            NoNetworkScreen { viewModel.retryConnection() }
        }

        uiState.isLoading && uiState.photos.isEmpty() -> {
            HomeSkeletonContent()
        }

        uiState.error != null && uiState.photos.isEmpty() -> {
            // TODO: 아무튼 재시도 화면?
            PrographyProgressIndicator()
        }

        else -> {
            HomeContent(
                bookmarks = uiState.bookmarks,
                photos = uiState.photos,
                gridState = gridState
            )
        }
    }
}

@Composable
private fun HomeContent(
    bookmarks: List<Bookmark>,
    photos: List<PhotoDetail>,
    gridState: LazyStaggeredGridState,
) {
    var selectedPhotoId by remember { mutableStateOf<String?>(null) }

    Surface(color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp)
        ) {
            if (bookmarks.isNotEmpty()) {
                item {
                    CategoryTitle(title = "북마크")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        flingBehavior = ScrollableDefaults.flingBehavior(),
                    ) {
                        itemsIndexed(
                            items = bookmarks,
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
                    Spacer(Modifier.height(20.dp))
                }
            }

            item {
                CategoryTitle(title = "최신 이미지")
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
                        photos,
                        key = { index, photo -> "${photo.id}_$index" }) { _, photo ->
                        PhotoCard(
                            cardModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            imageUrl = photo.urls.regular,
                            photoDescription = photo.description,
                            onClick = { selectedPhotoId = photo.id },
                            contentScale = ContentScale.Crop
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

@Composable
private fun HomeSkeletonContent() {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    Column {
        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(32.dp)
                .width(72.dp)
                .shimmer(shimmer)
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
        )

        Spacer(Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            userScrollEnabled = false
        ) {
            items(6) {
                Box(
                    modifier = Modifier
                        .height(128.dp)
                        .width(100.dp)
                        .shimmer(shimmer)
                        .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(32.dp)
                .width(100.dp)
                .shimmer(shimmer)
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            userScrollEnabled = false
        ) {
            items(10) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                        .shimmer(shimmer)
                        .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                )
            }
        }
    }
}
