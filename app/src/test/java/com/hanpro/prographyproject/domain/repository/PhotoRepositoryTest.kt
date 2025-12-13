package com.hanpro.prographyproject.domain.repository

import com.hanpro.prographyproject.data.model.Link
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.data.model.Urls
import com.hanpro.prographyproject.data.model.User
import com.hanpro.prographyproject.data.source.remote.PhotoRemoteDataSource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class PhotoRepositoryTest {

    @MockK
    private lateinit var photoRemoteDataSource: PhotoRemoteDataSource
    private lateinit var photoRepository: PhotoRepository

    private val mockPhotoDetail = PhotoDetail(
        id = "test-id",
        description = "Test Photo",
        urls = Urls(full = "https://example.com/photo.jpg", regular = "https://example.com/photo-regular.jpg"),
        tags = emptyList(),
        links = Link(download = "https://example.com/download"),
        user = User(username = "testuser")
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        photoRepository = PhotoRepository(photoRemoteDataSource)
    }

    // getLatestPhotos 테스트

    @Test
    fun `getLatestPhotos를 원격 DataSource에 위임하고 성공을 반환한다`() = runTest {
        // Given
        val page = 1
        val perPage = 30
        val mockPhotos = listOf(mockPhotoDetail, mockPhotoDetail.copy(id = "test-id-2"))
        // Result.success(): mockPhotos 값을 성공적인 결과로 반환하는 Result 객체를 생성하는 함수
        val successResult = Result.success(mockPhotos)

        coEvery { photoRemoteDataSource.getLatestPhotos(page, perPage) } returns successResult

        // When
        val result = photoRepository.getLatestPhotos(page, perPage)

        // Then
        assert(result.isSuccess)
        assert(mockPhotos == result.getOrNull())
        coVerify { photoRemoteDataSource.getLatestPhotos(page, perPage) }
    }

    @Test
    fun `getLatestPhotos를 원격 DataSource에 위임하고 실패를 반환한다`() = runTest {
        // Given
        val page = 1
        val perPage = 30
        val exception = RuntimeException("Network error")
        // Result.failure(): exception 값을 실패로 반환하는 Result 객체를 생성하는 함수
        val failureResult = Result.failure<List<PhotoDetail>>(exception)

        coEvery { photoRemoteDataSource.getLatestPhotos(page, perPage) } returns failureResult

        // When
        val result = photoRepository.getLatestPhotos(page, perPage)

        // Then
        assert(result.isFailure)
        assert(exception == result.exceptionOrNull())
        coVerify { photoRemoteDataSource.getLatestPhotos(page, perPage) }
    }

    @Test
    fun `getLatestPhotos는 DataSource에 올바른 매개변수를 전달한다`() = runTest {
        // Given
        val page = 5
        val perPage = 50
        val successResult = Result.success(emptyList<PhotoDetail>())

        coEvery { photoRemoteDataSource.getLatestPhotos(page, perPage) } returns successResult

        // When
        photoRepository.getLatestPhotos(page, perPage)

        // Then
        coVerify { photoRemoteDataSource.getLatestPhotos(page, perPage) }
        // confirmVerified 객체 이전에 명시적으로 호출된 verify 외에 다른 호출은 없었음을 확인
        confirmVerified(photoRemoteDataSource)
    }


    // getRandomPhotos 테스트

    @Test
    fun `getRandomPhotos를 원격 DataSource에 위임하고 성공을 반환한다`() = runTest {
        // Given
        val count = 10
        val mockPhotos = listOf(mockPhotoDetail, mockPhotoDetail.copy(id = "random-id"))
        val successResult = Result.success(mockPhotos)

        coEvery { photoRemoteDataSource.getRandomPhotos(count) } returns successResult

        // When
        val result = photoRepository.getRandomPhotos(count)

        // Then
        assert(result.isSuccess)
        assert(mockPhotos == result.getOrNull())
        coVerify { photoRemoteDataSource.getRandomPhotos(count) }
    }

    @Test
    fun `getRandomPhotos를 원격 DataSource에 위임하고 실패를 반환한다`() = runTest {
        // Given
        val count = 10
        val exception = RuntimeException("Network error")
        val failureResult = Result.failure<List<PhotoDetail>>(exception)

        coEvery { photoRemoteDataSource.getRandomPhotos(count) } returns failureResult

        // When
        val result = photoRepository.getRandomPhotos(count)

        // Then
        assert(result.isFailure)
        assert(exception == result.exceptionOrNull())
        coVerify { photoRemoteDataSource.getRandomPhotos(count) }
    }

    @Test
    fun `getRandomPhotos는 원격 DataSource에 올바른 매개변수를 전달한다`() = runTest {
        // Given
        val count = 20
        val successResult = Result.success(emptyList<PhotoDetail>())

        coEvery { photoRemoteDataSource.getRandomPhotos(count) } returns successResult

        // When
        photoRepository.getRandomPhotos(count)

        // Then
        coVerify { photoRemoteDataSource.getRandomPhotos(count) }
        confirmVerified(photoRemoteDataSource)
    }


    // getPhotoDetail 테스트
    @Test
    fun `getPhotoDetail을 원격 DataSource에 위임하고 성공을 반환한다`() = runTest {
        // Given
        val photoId = "test-id"
        val successResult = Result.success(mockPhotoDetail)

        coEvery { photoRemoteDataSource.getPhotoDetail(photoId) } returns successResult

        // When
        val result = photoRepository.getPhotoDetail(photoId)

        // Then
        assert(result.isSuccess)
        assert(mockPhotoDetail == result.getOrNull())
        coVerify { photoRemoteDataSource.getPhotoDetail(photoId) }
    }

    @Test
    fun `getPhotoDetail을 원격 DataSource에 위임하고 실패를 반환한다`() = runTest {
        // Given
        val photoId = "test-id"
        val exception = RuntimeException("Network error")
        val failureResult = Result.failure<PhotoDetail>(exception)

        coEvery { photoRemoteDataSource.getPhotoDetail(photoId) } returns failureResult

        // When
        val result = photoRepository.getPhotoDetail(photoId)

        // Then
        assert(result.isFailure)
        assert(exception == result.exceptionOrNull())
        coVerify { photoRemoteDataSource.getPhotoDetail(photoId) }
    }

    @Test
    fun `getPhotoDetail은 원격 DataSource에 올바른 ID를 전달한다`() = runTest {
        // Given
        val photoId = "test-id"
        val successResult = Result.success(mockPhotoDetail)

        coEvery { photoRemoteDataSource.getPhotoDetail(photoId) } returns successResult

        // When
        photoRepository.getPhotoDetail(photoId)

        // Then
        coVerify { photoRemoteDataSource.getPhotoDetail(photoId) }
        confirmVerified(photoRemoteDataSource)
    }


    // 전체 테스트

    @Test
    fun `Repository는 모든 작업에 대해 DataSource로 전달하는 역할을 한다`() = runTest {
        // Given
        val page = 1
        val perPage = 30
        val count = 10
        val photoId = "test-id"

        val latestPhotosResult = Result.success(listOf(mockPhotoDetail))
        val randomPhotosResult = Result.success(listOf(mockPhotoDetail))
        val photoDetailResult = Result.success(mockPhotoDetail)

        coEvery { photoRemoteDataSource.getLatestPhotos(page, perPage) } returns latestPhotosResult
        coEvery { photoRemoteDataSource.getRandomPhotos(count) } returns randomPhotosResult
        coEvery { photoRemoteDataSource.getPhotoDetail(photoId) } returns photoDetailResult

        // When
        val latestPhotos = photoRepository.getLatestPhotos(page, perPage)
        val randomPhotos = photoRepository.getRandomPhotos(count)
        val photoDetail = photoRepository.getPhotoDetail(photoId)

        // Then
        assert(latestPhotos == latestPhotosResult)
        assert(randomPhotos == randomPhotosResult)
        assert(photoDetail == photoDetailResult)
    }
}