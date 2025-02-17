package com.hanpro.prographyproject.data.source.local

import androidx.room.*

@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmarks")
    suspend fun getAllBookmarks(): List<Bookmark>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: Bookmark)

    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)
}