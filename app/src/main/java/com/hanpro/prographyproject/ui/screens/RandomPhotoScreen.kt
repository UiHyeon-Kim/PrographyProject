package com.hanpro.prographyproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.hanpro.prographyproject.ui.components.PhotoCardAnimation
import com.hanpro.prographyproject.ui.components.PhotoCardItems
import com.hanpro.prographyproject.ui.components.PrographyProgressIndicator
import com.hanpro.prographyproject.ui.dialog.PhotoDetailDialog
import com.hanpro.prographyproject.ui.viewmodel.PhotoViewModel

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
                    PhotoCardItems(
                        photo = it,
                        onNextClick = {},
                        onBookmarkClick = {},
                        onDetailClick = {},
                    )
                }
                PhotoCardAnimation(currentPhoto = currentPhoto) {
                    PhotoCardItems(
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
