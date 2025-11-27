package com.hanpro.prographyproject.domain.usecase

import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.domain.repository.PhotoRepository
import javax.inject.Inject

class GetLatestPhotosUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(page: Int, perPage: Int): Result<List<PhotoDetail>> =
        photoRepository.getLatestPhotos(page, perPage)
}