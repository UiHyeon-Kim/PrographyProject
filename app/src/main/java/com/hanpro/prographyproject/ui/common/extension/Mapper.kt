package com.hanpro.prographyproject.ui.common.extension

import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.data.source.local.Bookmark

fun PhotoDetail.toBookmark() = Bookmark(
    id = id,
    description = description ?: "",
    imageUrl = urls.regular,
)