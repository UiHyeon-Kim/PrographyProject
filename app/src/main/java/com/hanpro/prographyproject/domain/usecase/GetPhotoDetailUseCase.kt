package com.hanpro.prographyproject.domain.usecase

import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.domain.repository.PhotoRepository
import javax.inject.Inject

class GetPhotoDetailUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(id: String): Result<PhotoDetail> = when {
        id.isEmpty() -> Result.failure(IllegalArgumentException("Photo ID cannot be empty"))

        else -> photoRepository.getPhotoDetail(id)
    }
}