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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    // 네트워크 재시도 관련 변수
    private var retryJob: Job? = null
    private var retryCount = 0
    private val maxRetryCount = 5
    private val baseDelayMs = 2000L

    init {
        observeNetworkChanges()
        observeBookmarks()
    }

    private fun observeNetworkChanges() {
        viewModelScope.launch {
            // 아래 블럭은 networkState 가 변경될 때마다 실행됨
            networkManager.networkState.collect { isConnected ->
                if (isConnected) {
                    cancelRetry()
                    retryCount = 0

                    if (uiState.value.photos.isEmpty() || uiState.value.error != null) loadLatestPhotos()
                    if (uiState.value.randomPhotos.isEmpty() || uiState.value.error != null) loadRandomPhotos()
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "네트워크가 연결되어 있지 않습니다.") }

                    startAutoRetry()
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
                retryCount = 0

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
                retryCount = 0

                _uiState.update {
                    it.copy(
                        randomPhotos = _uiState.value.randomPhotos + photos,
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "알 수 없는 오류가 발생했습니다.") }
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

    /**
     * 네트워크 연결 재시도 시작
     */
    private fun startAutoRetry() {
        cancelRetry() // 기존 재시도 작업이 있으면 취소

        retryJob = viewModelScope.launch {
            // 네트워크가 연결되어 있지 않으면서 최대 횟수 전 동안
            while (retryCount < maxRetryCount && !isConnected.value) {
                // 비트 마스킹으로 지수 타임 재시도 - 2 -> 4 -> 8 -> 16 -> 32
                val delayTime = baseDelayMs * (1 shl retryCount.coerceAtMost(5))
                delay(delayTime)

                retryCount++

                // 네트워크 상태 확인. 복구되었으면 observeNetworkChanges가 처리
                if (networkManager.checkNetworkConnection()) break

                _uiState.update {
                    it.copy(error = "네트워크 연결 재시도 중 - (${retryCount}/${maxRetryCount})")
                }
            }

            if (retryCount >= maxRetryCount && !isConnected.value) {
                _uiState.update {
                    it.copy(error = "네트워크 연결 실패")
                }
            }
        }
    }

    /**
     * 네트워크 연결 재시도 취소
     */
    private fun cancelRetry() {
        retryJob?.cancel()
        retryJob = null
    }

    /**
     * 네트워크 수동 재연결 로직
     */
    fun retryConnection() {
        retryCount = 0
        cancelRetry()

        if (networkManager.checkNetworkConnection()) {
            if (uiState.value.photos.isEmpty() || uiState.value.error != null) loadLatestPhotos()
            if (uiState.value.randomPhotos.isEmpty() || uiState.value.error != null) loadRandomPhotos()
        } else {
            startAutoRetry()
        }
    }

    override fun onCleared() {
        super.onCleared()
        cancelRetry() // 뷰모델 종료 시 재시도 작업 취소
    }
}