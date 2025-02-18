package com.hanpro.prographyproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.data.source.local.Bookmark
import com.hanpro.prographyproject.data.source.remote.UnsplashApi
import com.hanpro.prographyproject.domain.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhotoDetailUiState(
    val photo: PhotoDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isBookmarked: Boolean = false,
)

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    private val unsplashApi: UnsplashApi,
    private val bookmarkRepository: BookmarkRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PhotoDetailUiState())
    val uiState: StateFlow<PhotoDetailUiState> = _uiState

    fun loadPhotoDetail(photoId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val photo = unsplashApi.getPhotoDetail(photoId)
                val bookmarkList = bookmarkRepository.getBookmarks().first()
                val bookmarked = bookmarkList.any { it.id == photoId }
                _uiState.value = PhotoDetailUiState(
                    photo = photo,
                    isLoading = false,
                    error = null,
                    isBookmarked = bookmarked
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun addBookmark(photo: PhotoDetail) {
        viewModelScope.launch {
            bookmarkRepository.addBookmark(
                Bookmark(
                    id = photo.id,
                    description = photo.description ?: "",
                    imageUrl = photo.urls.regular
                )
            )
            _uiState.value = _uiState.value.copy(isBookmarked = true)
        }

    }

    fun deleteBookmark(photo: PhotoDetail) {
        viewModelScope.launch {
            bookmarkRepository.deleteBookmark(
                Bookmark(
                    id = photo.id,
                    description = photo.description ?: "",
                    imageUrl = photo.urls.regular
                )
            )
            _uiState.value = _uiState.value.copy(isBookmarked = false)
        }
    }
}