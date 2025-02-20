package com.hanpro.prographyproject.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey val id: String,
    val description: String,
    val imageUrl: String,
)
