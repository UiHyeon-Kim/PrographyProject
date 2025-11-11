package com.hanpro.prographyproject.domain.usecase

import com.hanpro.prographyproject.domain.repository.BookmarkRepository
import javax.inject.Inject

class GetBookmarksUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    operator fun invoke() = bookmarkRepository.getBookmarks()
}