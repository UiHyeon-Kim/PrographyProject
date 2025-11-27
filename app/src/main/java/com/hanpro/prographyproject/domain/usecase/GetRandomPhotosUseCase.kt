package com.hanpro.prographyproject.domain.usecase

import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.domain.repository.PhotoRepository
import javax.inject.Inject

class GetRandomPhotosUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(count: Int): Result<List<PhotoDetail>> =
        photoRepository.getRandomPhotos(count)
}