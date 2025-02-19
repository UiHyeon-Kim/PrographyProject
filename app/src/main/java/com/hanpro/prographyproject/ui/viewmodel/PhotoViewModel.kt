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
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhotoUiState(
    val bookmarks: List<Bookmark> = emptyList(),
    val photos: List<PhotoDetail> = emptyList(),
    val randomPhotos: List<PhotoDetail> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val unsplashApi: UnsplashApi,
    private val bookmarkRepository: BookmarkRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PhotoUiState())
    val uiState: StateFlow<PhotoUiState> = _uiState

    init {
        viewModelScope.launch {
            bookmarkRepository.getBookmarks().collect() { bookmarks ->
                _uiState.value = _uiState.value.copy(bookmarks = bookmarks)
            }
        }
    }

    fun loadLatestPhotos(page: Int = 1, perPage: Int = 30) {
        viewModelScope.launch {
            try {
                val photos = unsplashApi.photoPages(page, perPage)
                _uiState.value = if (page == 1) {
                    _uiState.value.copy(
                        photos = photos,
                        isLoading = false,
                        error = null
                    )
                } else {
                    _uiState.value.copy(
                        photos = (_uiState.value.photos + photos).distinctBy { it.id },
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun loadRandomPhotos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val photos = unsplashApi.getRandomPhoto()
                _uiState.value = _uiState.value.copy(
                    randomPhotos = photos,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}