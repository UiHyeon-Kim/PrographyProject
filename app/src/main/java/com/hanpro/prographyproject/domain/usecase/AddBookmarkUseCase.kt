package com.hanpro.prographyproject.domain.usecase

import com.hanpro.prographyproject.data.source.local.Bookmark
import com.hanpro.prographyproject.domain.repository.BookmarkRepository
import javax.inject.Inject

class AddBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    /**
     * 주어진 북마크를 저장하도록 저장소에 위임한다.
     *
     * @param bookmark 저장할 북마크 객체
     */
    suspend operator fun invoke(bookmark: Bookmark) = bookmarkRepository.addBookmark(bookmark)
}