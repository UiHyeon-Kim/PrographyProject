package com.hanpro.prographyproject.domain.usecase

import com.hanpro.prographyproject.data.source.local.Bookmark
import com.hanpro.prographyproject.domain.repository.BookmarkRepository
import javax.inject.Inject

class AddBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(bookmark: Bookmark) = bookmarkRepository.addBookmark(bookmark)
}