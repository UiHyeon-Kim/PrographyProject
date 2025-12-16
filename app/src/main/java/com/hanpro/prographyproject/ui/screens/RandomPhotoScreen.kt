package com.hanpro.prographyproject.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hanpro.prographyproject.common.utils.NetworkEvent
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.ui.components.PhotoCardItems
import com.hanpro.prographyproject.ui.components.PrographyProgressIndicator
import com.hanpro.prographyproject.ui.dialog.PhotoDetailDialog
import com.hanpro.prographyproject.ui.viewmodel.PhotoViewModel
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * 랜덤 사진 목록을 페이저로 표시하고 페이저 내비게이션, 북마크 추가 및 사진 상세 보기 상호작용을 처리한다.
 *
 * 초기에 사진 목록이 비어 있으면 데이터를 로드하고 로딩 인디케이터를 표시한다. 페이저의 현재 페이지가 목록 끝에 가까워지면 추가 사진을 사전 로드한다.
 * 각 사진 카드에서 다음 페이지로 이동하거나 북마크를 추가하면 페이저를 다음 페이지로 이동시키고 내부 인덱스를 갱신하며, 사진 상세보기는 다이얼로그로 표시된다.
 */
@OptIn(FlowPreview::class)
@Composable
fun RandomPhotoScreen(
    viewModel: PhotoViewModel = hiltViewModel(),
    pagerState: PagerState = rememberPagerState(pageCount = { viewModel.uiState.value.randomPhotos.size })
) {
    val uiState by viewModel.uiState.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val networkEvent by viewModel.networkEvent.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (uiState.randomPhotos.isEmpty()) {
            viewModel.loadRandomPhotos()
        }
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

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .filter { page -> uiState.randomPhotos.isNotEmpty() && page >= uiState.randomPhotos.size - 3 }
            .debounce(300)
            .collect { if (isConnected && !uiState.isRandomLoading) viewModel.loadRandomPhotos() }
    }

    when {
        !isConnected -> {
            NoNetworkScreen { viewModel.retryConnection() }
        }

        uiState.isRandomLoading && uiState.randomPhotos.isEmpty() -> {
            RandomSkeletonContent()
        }

        uiState.error != null && uiState.randomPhotos.isEmpty() -> {
            PrographyProgressIndicator()
            // TODO: 네트워크 확인 및 재연결 로직
        }

        else -> {
            RandomPhotoContent(
                pagerState = pagerState,
                randomPhotos = uiState.randomPhotos,
                onIndexIncrement = { viewModel.incrementIndex() },
                onBookmarkAdd = { photo -> viewModel.addBookmark(photo) },
                isBookmarked = { photoId -> viewModel.isBookmarked(photoId) }
            )
        }
    }
}

/**
 * 사진을 수평 페이저로 표시하고 북마크 및 상세 조회 기능을 제공합니다.
 *
 * @param pagerState 페이저의 상태 및 페이지 위치.
 * @param randomPhotos 표시할 사진 목록.
 * @param onIndexIncrement 페이지 이동 시 호출되는 콜백.
 * @param onBookmarkAdd 사진 북마크 시 호출되는 콜백.
 * @param isBookmarked 사진 북마크 여부를 확인하는 함수.
 */
@Composable
private fun RandomPhotoContent(
    pagerState: PagerState,
    randomPhotos: List<PhotoDetail>,
    onIndexIncrement: () -> Unit,
    onBookmarkAdd: (PhotoDetail) -> Unit,
    isBookmarked: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var selectedPhotoId by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 80.dp)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.testTag("pager"),
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 8.dp,
        ) { page ->
            val photo = randomPhotos[page]
            PhotoCardItems(
                photo = photo,
                onNextClick = {
                    coroutineScope.launch {
                        val nextPage = (page + 1).coerceAtMost(pagerState.pageCount - 1)
                        if (nextPage > page) {
                            pagerState.animateScrollToPage(nextPage)
                            onIndexIncrement()
                        }
                    }
                },
                onBookmarkClick = {
                    coroutineScope.launch {
                        if (!isBookmarked(photo.id)) {
                            onBookmarkAdd(photo)
                            val nextPage = (page + 1).coerceAtMost(pagerState.pageCount - 1)
                            if (nextPage > page) {
                                pagerState.animateScrollToPage(nextPage)
                                onIndexIncrement()
                            }
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

/**
 * 로딩 중에 화면 전체를 채우는 쉬머(shimmer) 기반 골격 플레이스홀더를 표시합니다.
 *
 * 루트 레이아웃은 상태 표시줄 및 상하 패딩을 포함해 available space를 채우며,
 * 내부 콘텐츠는 수평 패딩과 라운드된 회색 배경 위에 쉬머 애니메이션을 적용합니다.
 *
 * @param modifier 루트 컨테이너에 추가로 적용할 Modifier. 기본값은 Modifier입니다.
 *
 * 테스트 목적을 위해 뷰에 "random_skeleton" 테스트 태그가 설정됩니다.
 */
@Composable
private fun RandomSkeletonContent(
    modifier: Modifier = Modifier
) {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 80.dp)
            .statusBarsPadding()
            .testTag("random_skeleton"),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .shimmer(shimmer)
                .background(Color.LightGray, shape = RoundedCornerShape(15.dp))
        )
    }
}