package com.hanpro.prographyproject.data.source.remote

import com.hanpro.prographyproject.data.model.PhotoDetail
import javax.inject.Inject

class PhotoRemoteDataSource @Inject constructor(
    private val unsplashApiService: UnsplashApiService
) {
    /**
     * 최신 사진 목록을 가져옵니다.
     *
     * @param page 가져올 페이지 번호(1부터 시작).
     * @param perPage 한 페이지당 가져올 사진 수.
     * @return 성공 시 `PhotoDetail` 객체들의 리스트를 담은 `Result`, 실패 시 예외를 포함한 실패 `Result`.
     */
    suspend fun getLatestPhotos(page: Int = 1, perPage: Int = 30): Result<List<PhotoDetail>> = runCatching {
        unsplashApiService.getLatestPhotos(page, perPage)
    }

    /**
     * 지정된 개수의 무작위 사진 상세 정보를 요청한다.
     *
     * @param count 가져올 사진의 수. 기본값은 10이다.
     * @return 성공 시 PhotoDetail 객체 목록을 담은 `Result`, 실패 시 예외를 담은 실패 `Result`.
     */
    suspend fun getRandomPhotos(count: Int = 10): Result<List<PhotoDetail>> = runCatching {
        unsplashApiService.getRandomPhotos(count)
    }

    /**
     * ID에 해당하는 사진의 상세 정보를 가져옵니다.
     *
     * @param id 조회할 사진의 고유 식별자.
     * @return 요청이 성공하면 해당 사진의 `PhotoDetail`을 담은 성공 `Result`, 실패하면 예외를 담은 실패 `Result`.
     */
    suspend fun getPhotoDetail(id: String): Result<PhotoDetail> = runCatching {
        unsplashApiService.getPhotoDetail(id)
    }
}