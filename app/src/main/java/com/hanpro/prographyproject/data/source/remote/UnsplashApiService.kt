package com.hanpro.prographyproject.data.source.remote

import com.hanpro.prographyproject.data.model.PhotoDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashApiService {
    /**
     * 최신 사진 목록을 페이지 단위로 조회한다.
     *
     * @param page 가져올 페이지 번호 (기본값 1).
     * @param perPage 한 페이지당 가져올 항목 수 (기본값 30).
     * @return 요청한 페이지에 해당하는 최신 사진들의 `PhotoDetail` 리스트.
     */
    @GET("photos")
    suspend fun getLatestPhotos(
        @Query("page") page: Int = 1,           // 페이지
        @Query("per_page") perPage: Int = 30,   // 페이지 당 항목
    ): List<PhotoDetail>

    /**
     * 특정 사진의 상세 정보를 가져옵니다.
     *
     * @param id 조회할 사진의 고유 식별자
     * @return 요청한 사진의 상세 정보를 담은 `PhotoDetail` 객체
     */
    @GET("photos/{id}")
    suspend fun getPhotoDetail(@Path("id") id: String): PhotoDetail

    /**
     * 지정한 수만큼의 랜덤 사진 목록을 가져옵니다.
     *
     * @param count 요청할 랜덤 사진 수 (기본값 10)
     * @return 랜덤으로 선택된 `PhotoDetail` 객체들의 리스트
     */
    @GET("photos/random")
    suspend fun getRandomPhotos(
        @Query("count") count: Int = 10,
    ): List<PhotoDetail>
}