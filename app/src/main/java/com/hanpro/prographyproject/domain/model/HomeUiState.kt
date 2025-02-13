package com.hanpro.prographyproject.domain.model

import com.hanpro.prographyproject.data.model.PhotoDetail

data class HomeUiState(
    val bookmarks: List<PhotoDetail> = emptyList(),
    val latestPhotos: List<PhotoDetail> = emptyList(),
    val isLoading: Boolean = false, // 로딩 상태
    val error: String? = null,
    val success: String? = null,
)
