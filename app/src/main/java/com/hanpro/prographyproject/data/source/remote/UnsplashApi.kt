package com.hanpro.prographyproject.data.source.remote

import com.hanpro.prographyproject.data.model.LatestPhoto
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {
    @GET("photos")
    suspend fun photoPages(
        @Query("page") page: Int = 1,           // 페이지
        @Query("per_page") perPage: Int = 10,   // 페이지 당 항목
    ): List<LatestPhoto>
}