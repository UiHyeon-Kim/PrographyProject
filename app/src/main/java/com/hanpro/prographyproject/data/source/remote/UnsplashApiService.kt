package com.hanpro.prographyproject.data.source.remote

import com.hanpro.prographyproject.data.model.PhotoDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashApiService {
    @GET("photos")
    suspend fun getPhotoPages(
        @Query("page") page: Int = 1,           // 페이지
        @Query("per_page") perPage: Int = 30,   // 페이지 당 항목
    ): List<PhotoDetail>

    @GET("photos/{id}")
    suspend fun getPhotoDetail(@Path("id") id: String): PhotoDetail

    @GET("photos/random")
    suspend fun getRandomPhoto(
        @Query("count") count: Int = 10,
    ): List<PhotoDetail>
}