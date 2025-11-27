package com.hanpro.prographyproject.domain.repository

import com.hanpro.prographyproject.data.source.remote.PhotoRemoteDataSource
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val photoRemoteDataSource: PhotoRemoteDataSource,
) {
    suspend fun getLatestPhotos(page: Int, perPage: Int) = photoRemoteDataSource.getLatestPhotos(page, perPage)

    suspend fun getRandomPhotos(count: Int) = photoRemoteDataSource.getRandomPhotos(count)

    suspend fun getPhotoDetail(id: String) = photoRemoteDataSource.getPhotoDetail(id)
}