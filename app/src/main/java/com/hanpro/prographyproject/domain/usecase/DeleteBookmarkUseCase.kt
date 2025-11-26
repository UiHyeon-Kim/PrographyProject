package com.hanpro.prographyproject.domain.usecase

import com.hanpro.prographyproject.data.source.local.Bookmark
import com.hanpro.prographyproject.domain.repository.BookmarkRepository
import javax.inject.Inject

class DeleteBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    /**
     * 주어진 북마크를 삭제한다.
     *
     * @param bookmark 삭제할 북마크
     */
    suspend operator fun invoke(bookmark: Bookmark) = bookmarkRepository.deleteBookmark(bookmark)
}