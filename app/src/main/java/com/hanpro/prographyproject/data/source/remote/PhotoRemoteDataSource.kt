package com.hanpro.prographyproject.data.source.remote

import com.hanpro.prographyproject.data.model.PhotoDetail
import javax.inject.Inject

class PhotoRemoteDataSource @Inject constructor(
    private val unsplashApiService: UnsplashApiService
) {
    suspend fun getLatestPhotos(page: Int = 1, perPage: Int = 30): Result<List<PhotoDetail>> = runCatching {
        unsplashApiService.getPhotoPages(page, perPage)
    }

    suspend fun getRandomPhotos(count: Int = 10): Result<List<PhotoDetail>> = runCatching {
        unsplashApiService.getRandomPhoto(count)
    }

    suspend fun getPhotoDetail(id: String): Result<PhotoDetail> = runCatching {
        unsplashApiService.getPhotoDetail(id)
    }
}