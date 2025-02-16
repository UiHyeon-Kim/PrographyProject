package com.hanpro.prographyproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.data.source.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PhotoUiState(
    val bookmarks: List<PhotoDetail> = emptyList(),
    val photos: List<PhotoDetail> = emptyList(),
    val isLoading: Boolean = true, // 로딩 상태
    val error: String? = null,
)

/*data class PhotoDetailUiState(
    val photoDetail: PhotoDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    // 북마크? 좋아요?
)*/

class PhotoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PhotoUiState())
    val uiState: StateFlow<PhotoUiState> = _uiState

    private val _randomPhoto = MutableStateFlow<List<PhotoDetail>>(emptyList())
    val randomPhoto: StateFlow<List<PhotoDetail>> = _randomPhoto

    // TODO 1 - 30 변경
    fun loadLatestPhotos(page: Int = 0, perPage: Int = 0) {
        viewModelScope.launch {
            try {
                val photos = RetrofitClient.unsplashApi.photoPages(page, perPage)
                _uiState.value = _uiState.value.copy(
                    photos = _uiState.value.photos + photos,
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

    fun loadRandomPhoto() {
        viewModelScope.launch {
            try {
                val photo = RetrofitClient.unsplashApi.getRandomPhoto()
                _randomPhoto.value = photo
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /*fun loadPhotoById(id: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.unsplashApi.getPhoto(id)

            } catch (e: Exception) {

            }
        }
    }*/
}