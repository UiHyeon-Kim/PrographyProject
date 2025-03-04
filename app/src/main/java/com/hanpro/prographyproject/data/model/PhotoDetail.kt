package com.hanpro.prographyproject.data.model

data class PhotoDetail(
    val id: String,
    val description: String?,
    val urls: Urls,
    val tags: List<Tag>? = null,
    val links: Link,
    val user: User,
)

data class Urls(
    val full: String,
    val regular: String,
    val small: String,
)

data class Tag(
    val title: String
)

data class Link(
    val download: String
)

data class User(
    val username: String
)