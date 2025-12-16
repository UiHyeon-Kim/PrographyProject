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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhotoDetailUiState(
    val photo: PhotoDetail? = null,
    val isLoading: Boolean = true,
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

    /**
     * 지정한 사진 ID에 해당하는 사진 상세 정보를 불러오고 현재 북마크 상태를 확인하여 UI 상태를 갱신합니다.
     *
     * 호출 시 로딩 상태를 활성화하고 기존 오류를 지우며, 사용 사례에서 결과를 받아와 성공하면
     * photo, isLoading, error, isBookmarked 필드를 적절히 업데이트합니다. 실패하면 isLoading을 해제하고
     * 오류 메시지를 UI 상태의 `error`에 설정합니다.
     *
     * @param photoId 상세를 가져올 사진의 고유 ID
     */
    fun loadPhotoDetail(photoId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getPhotoDetailUseCase(photoId)
                .onSuccess { photo ->
                    val bookmarkList = getBookmarksUseCase().first()
                    val bookmarked = bookmarkList.any { it.id == photoId }
                    _uiState.update {
                        it.copy(
                            photo = photo,
                            isLoading = false,
                            error = null,
                            isBookmarked = bookmarked
                        )
                    }
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    /**
     * 주어진 사진을 북마크 목록에 추가하고 뷰 모델의 UI 상태에서 북마크 플래그를 활성화한다.
     *
     * @param photo 북마크로 추가할 사진 상세 정보
     */
    fun addBookmark(photo: PhotoDetail) {
        viewModelScope.launch {
            runCatching { addBookmarkUseCase(photo.toBookmark()) }
                .onSuccess { _uiState.update { it.copy(isBookmarked = true) } }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    /**
     * 지정한 사진을 북마크에서 제거하고 UI 상태의 북마크 플래그를 해제합니다.
     *
     * @param photo 제거할 사진의 상세 정보
     */
    fun deleteBookmark(photo: PhotoDetail) {
        viewModelScope.launch {
            runCatching { deleteBookmarkUseCase(photo.toBookmark()) }
                .onSuccess { _uiState.update { it.copy(isBookmarked = false) } }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }
}