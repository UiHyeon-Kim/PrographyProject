package com.hanpro.prographyproject.domain.usecase

import com.hanpro.prographyproject.domain.repository.BookmarkRepository
import javax.inject.Inject

class GetBookmarksUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    /**
     * 저장된 북마크 목록을 가져옵니다.
     *
     * @return 북마크 목록을 포함하는 결과값.
     */
    operator fun invoke() = bookmarkRepository.getBookmarks()
}