package com.hanpro.prographyproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.data.source.remote.UnsplashApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhotoDetailUiState(
    val photo: PhotoDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    private val unsplashApi: UnsplashApi,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PhotoDetailUiState())
    val uiState: StateFlow<PhotoDetailUiState> = _uiState

    fun loadPhotoDetail(photoId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val photo = unsplashApi.getPhotoDetail(photoId)
                _uiState.value = _uiState.value.copy(
                    photo = photo,
                    isLoading = false,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
