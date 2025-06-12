package com.hanpro.prographyproject.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.hanpro.prographyproject.R
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.ui.components.PrographyButtonIcon
import com.hanpro.prographyproject.ui.components.PrographyIconButton
import com.hanpro.prographyproject.ui.components.PrographyProgressIndicator
import com.hanpro.prographyproject.ui.dialog.PhotoDetailDialog
import com.hanpro.prographyproject.ui.viewmodel.PhotoViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun RandomPhotoScreen(
    viewModel: PhotoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedPhotoId by remember { mutableStateOf<String?>(null) }

    var currentIndex = uiState.randomPhotoIndex

    LaunchedEffect(currentIndex) {
        if (currentIndex >= uiState.randomPhotos.size - 2) {
            viewModel.loadRandomPhotos()
        }
    }

    if (uiState.randomPhotos.isEmpty()) {
        LaunchedEffect(Unit) { viewModel.loadRandomPhotos() }
        PrographyProgressIndicator()
        return
    }

    val currentPhoto = uiState.randomPhotos.getOrNull(currentIndex) ?: return
    val nextPhoto = uiState.randomPhotos.getOrNull(currentIndex + 1)

    Box(modifier = Modifier.fillMaxSize()) {
        Popup(
            properties = PopupProperties(
                usePlatformDefaultWidth = false,
                dismissOnClickOutside = false,
                focusable = false
            ),
            alignment = Alignment.TopStart,
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(start = 24.dp, top = (70 + 28).dp, end = 24.dp, bottom = (84 + 45).dp)
                    .zIndex(2f),
                contentAlignment = Alignment.Center
            ) {
                nextPhoto?.let {
                    PhotoCardItem(
                        photo = it,
                        onNextClick = {},
                        onBookmarkClick = {},
                        onDetailClick = {},
                    )
                }
                CardAnimation(currentPhoto = currentPhoto) {
                    PhotoCardItem(
                        photo = currentPhoto,
                        onNextClick = { viewModel.incrementIndex() },
                        onBookmarkClick = { photo ->
                            if (!viewModel.isBookmarked(photo.id)) {
                                viewModel.addBookmark(photo)
                                viewModel.incrementIndex()
                            } else {
                                viewModel.deleteBookmark(photo)
                            }
                        },
                        onDetailClick = { selectedPhotoId = it },
                    )
                }
            }
        }
    }

    selectedPhotoId?.let { photoId ->
        PhotoDetailDialog(photoId = photoId, onClose = { selectedPhotoId = null })
    }
}

@Composable
fun PhotoCardItem(
    photo: PhotoDetail,
    onNextClick: () -> Unit,
    onBookmarkClick: (PhotoDetail) -> Unit,
    onDetailClick: (String) -> Unit,
) {
    // 필름 사진 배경
    Card(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, shape = RoundedCornerShape(15.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(15.dp)
            ),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
    ) {
        Column(modifier = Modifier.background(Color.Transparent)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)
            ) {
                RandomPhotoItem(randomPhoto = photo)
            }
            // 버튼 행
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(start = 44.dp, top = 24.dp, end = 44.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 다음 사진 버튼
                PrographyIconButton(
                    onClick = { onNextClick() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    PrographyButtonIcon(
                        iconId = R.drawable.x,
                        content = "back",
                    )
                }
                Spacer(modifier = Modifier.width(32.dp))

                // 북마크 버튼
                PrographyIconButton(
                    onClick = { onBookmarkClick(photo) },
                    modifier = Modifier.size(72.dp),
                    buttonColor = MaterialTheme.colorScheme.primary,
                    showBorder = false,
                ) {
                    PrographyButtonIcon(
                        iconId = R.drawable.bookmark,
                        content = "bookmark",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))
                // 디테일 버튼
                PrographyIconButton(
                    onClick = { onDetailClick(photo.id) },
                    modifier = Modifier.padding(8.dp)
                ) {
                    PrographyButtonIcon(
                        iconId = R.drawable.information,
                        content = "information",
                    )
                }
            }
        }
    }
}

@Composable
fun RandomPhotoItem(randomPhoto: PhotoDetail) {
    // 이미지
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, top = 12.dp, end = 12.dp)
    ) {
        AsyncImage(
            model = randomPhoto.urls.regular,
            contentDescription = randomPhoto.description ?: "Random Photo",
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black, shape = RoundedCornerShape(10.dp)),
        )
    }
}

@Composable
fun CardAnimation(
    currentPhoto: PhotoDetail,
    viewModel: PhotoViewModel = hiltViewModel(),
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    var offsetX = remember { Animatable(0f) }
    var offsetY = remember { Animatable(0f) }
    val threshold = 200f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .graphicsLayer { rotationZ = (offsetX.value / threshold) * 10f }
            .zIndex(2f)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        when {
                            offsetX.value > threshold -> { // 오른쪽 스와이프
                                coroutineScope.launch {
                                    viewModel.addBookmark(currentPhoto)
                                    animateOutCard(offsetX, offsetY, toRight = true) {
                                        viewModel.incrementIndex()
                                    }
                                }
                            }

                            offsetX.value < -threshold -> { // 왼쪽 스와이프
                                coroutineScope.launch {
                                    animateOutCard(offsetX, offsetY, toRight = false) {
                                        viewModel.incrementIndex()
                                    }
                                }
                            }

                            else -> {
                                coroutineScope.launch {
                                    offsetX.animateTo(0f, tween(durationMillis = 300))
                                    offsetY.animateTo(0f, tween(durationMillis = 300))
                                }
                            }
                        }
                    }
                ) { change, dragAmount ->
                    change.consume()
                    coroutineScope.launch {
                        offsetX.snapTo(offsetX.value + dragAmount.x)
                        offsetY.snapTo(offsetY.value + dragAmount.y)
                    }
                }
            }
    ) { content() }
}

suspend fun animateOutCard(
    offsetX: Animatable<Float, AnimationVector1D>,
    offsetY: Animatable<Float, AnimationVector1D>,
    toRight: Boolean,
    onComplete: () -> Unit,
) {
    val targetX = if (toRight) 1000f else -1000f
    offsetX.animateTo(targetValue = targetX, animationSpec = tween(durationMillis = 300))
    offsetY.animateTo(targetValue = 0f, animationSpec = tween(durationMillis = 300))
    offsetX.snapTo(0f)
    offsetY.snapTo(0f)
    onComplete()
}