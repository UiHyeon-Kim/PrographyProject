package com.hanpro.prographyproject.domain.repository

import com.hanpro.prographyproject.data.source.local.Bookmark
import com.hanpro.prographyproject.data.source.local.BookmarkDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookmarkRepository @Inject constructor(
    private val bookmarkDao: BookmarkDao
) {
    fun getBookmarks(): Flow<List<Bookmark>> = bookmarkDao.getAllBookmarks()

    suspend fun addBookmark(bookmark: Bookmark) = bookmarkDao.insertBookmark(bookmark)

    suspend fun deleteBookmark(bookmark: Bookmark) = bookmarkDao.deleteBookmark(bookmark)
}