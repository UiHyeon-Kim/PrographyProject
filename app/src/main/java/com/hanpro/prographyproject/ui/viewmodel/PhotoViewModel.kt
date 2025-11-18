package com.hanpro.prographyproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanpro.prographyproject.common.utils.NetworkManager
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.data.source.local.Bookmark
import com.hanpro.prographyproject.data.source.remote.UnsplashApi
import com.hanpro.prographyproject.domain.usecase.AddBookmarkUseCase
import com.hanpro.prographyproject.domain.usecase.DeleteBookmarkUseCase
import com.hanpro.prographyproject.domain.usecase.GetBookmarksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhotoUiState(
    val bookmarks: List<Bookmark> = emptyList(),
    val photos: List<PhotoDetail> = emptyList(),
    val randomPhotos: List<PhotoDetail> = emptyList(),
    val randomPhotoIndex: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val unsplashApi: UnsplashApi,
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val addBookmarkUseCase: AddBookmarkUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase,
    private val networkManager: NetworkManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PhotoUiState())
    val uiState: StateFlow<PhotoUiState> = _uiState

    val isConnected = networkManager.networkState

    init {
        observeNetworkChanges()
        observeBookmarks()
    }

    private fun observeNetworkChanges() {
        viewModelScope.launch {
            networkManager.networkState.collect { isConnected ->
                if (isConnected) {
                    if (uiState.value.photos.isEmpty()) loadLatestPhotos()
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "네트워크가 연결되어 있지 않습니다.") }
                }
            }
        }
    }

    private fun observeBookmarks() {
        viewModelScope.launch {
            getBookmarksUseCase().collect { bookmarks ->
                _uiState.update { it.copy(bookmarks = bookmarks) }
            }
        }
    }

    fun loadLatestPhotos(page: Int = 1, perPage: Int = 30) {
        // 네트워크 연결이 안 되어있다면 API 호출 막음
        if (!isConnected.value) {
            _uiState.update { it.copy(isLoading = false, error = "네트워크 연결이 필요합니다.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            runCatching {
                unsplashApi.photoPages(page, perPage)
            }.onSuccess { photos ->
                val newPhotos = if (page == 1) photos
                else (_uiState.value.photos + photos).distinctBy { it.id }

                _uiState.update { it.copy(photos = newPhotos, isLoading = false, error = null) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "알 수 없는 오류가 발생했습니다.") }
            }
        }
    }

    fun loadRandomPhotos() {
        if (!isConnected.value) {
            _uiState.update { it.copy(isLoading = false, error = "네트워크 연결이 필요합니다.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            runCatching {
                unsplashApi.getRandomPhoto(10)
            }.onSuccess { photos ->
                _uiState.update { it.copy(randomPhotos = _uiState.value.randomPhotos + photos, isLoading = false, error = null) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun incrementIndex() {
        _uiState.update { it.copy(randomPhotoIndex = _uiState.value.randomPhotoIndex + 1) }
    }

    fun addBookmark(photo: PhotoDetail) {
        viewModelScope.launch {
            val bookmark = Bookmark(
                id = photo.id,
                description = photo.description ?: "",
                imageUrl = photo.urls.regular
            )
            addBookmarkUseCase(bookmark)
            val updatedBookmarks = getBookmarksUseCase().first()
            _uiState.update { it.copy(bookmarks = updatedBookmarks) }
        }
    }

    fun deleteBookmark(photo: PhotoDetail) {
        viewModelScope.launch {
            val bookmark = Bookmark(
                id = photo.id,
                description = photo.description ?: "",
                imageUrl = photo.urls.regular
            )
            deleteBookmarkUseCase(bookmark)
            val updatedBookmarks = getBookmarksUseCase().first()
            _uiState.update { it.copy(bookmarks = updatedBookmarks) }
        }
    }

    fun isBookmarked(photoId: String): Boolean = _uiState.value.bookmarks.any { it.id == photoId }
}