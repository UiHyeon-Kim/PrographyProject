package com.hanpro.prographyproject.data.model

data class PhotoDetail(
    val id: String,
    val description: String?,
    val urls: Urls,
    val tags: List<Tag>? = null,
)

data class Urls(
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String,
)

data class Tag(
    val title: String
)