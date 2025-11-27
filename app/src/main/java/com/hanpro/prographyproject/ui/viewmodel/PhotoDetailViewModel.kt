package com.hanpro.prographyproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.domain.usecase.AddBookmarkUseCase
import com.hanpro.prographyproject.domain.usecase.DeleteBookmarkUseCase
import com.hanpro.prographyproject.domain.usecase.GetBookmarksUseCase
import com.hanpro.prographyproject.domain.usecase.GetPhotoDetailUseCase
import com.hanpro.prographyproject.ui.common.extension.toBookmark
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
    private val getPhotoDetailUseCase: GetPhotoDetailUseCase,
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val addBookmarkUseCase: AddBookmarkUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PhotoDetailUiState())
    val uiState: StateFlow<PhotoDetailUiState> = _uiState

    fun loadPhotoDetail(photoId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            getPhotoDetailUseCase(photoId)
                .onSuccess { photo ->
                    val bookmarkList = getBookmarksUseCase().first()
                    val bookmarked = bookmarkList.any { it.id == photoId }
                    _uiState.value = PhotoDetailUiState(
                        photo = photo,
                        isLoading = false,
                        error = null,
                        isBookmarked = bookmarked
                    )
                }.onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
        }
    }

    fun addBookmark(photo: PhotoDetail) {
        viewModelScope.launch {
            addBookmarkUseCase(photo.toBookmark())
            _uiState.value = _uiState.value.copy(isBookmarked = true)
        }
    }

    fun deleteBookmark(photo: PhotoDetail) {
        viewModelScope.launch {
            deleteBookmarkUseCase(photo.toBookmark())
            _uiState.value = _uiState.value.copy(isBookmarked = false)
        }
    }
}