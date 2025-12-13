package com.hanpro.prographyproject.domain.usecase

import com.hanpro.prographyproject.data.model.*
import com.hanpro.prographyproject.domain.repository.PhotoRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GetPhotoDetailUseCaseTest {

    @MockK
    private lateinit var photoRepository: PhotoRepository
    private lateinit var getPhotoDetailUseCase: GetPhotoDetailUseCase

    private val mockPhotoDetail = PhotoDetail(
        id = "detail-usecase-id",
        description = "Detail UseCase test photo",
        urls = Urls(full = "https://example.com/full.jpg", regular = "https://example.com/regular.jpg"),
        tags = listOf(Tag(title = "test"), Tag(title = "usecase")),
        links = Link(download = "https://example.com/download"),
        user = User(username = "detailuser")
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        getPhotoDetailUseCase = GetPhotoDetailUseCase(photoRepository)
    }

    @Test
    fun `Repository가 성공을 반환할 때 UseCase는 성공을 반환한다`() = runTest {
        // Given
        val photoId = "test-photo-id"
        val successResult = Result.success(mockPhotoDetail)

        coEvery { photoRepository.getPhotoDetail(photoId) } returns successResult

        // When
        val result = getPhotoDetailUseCase(photoId)

        // Then
        assert(result.isSuccess)
        assert(mockPhotoDetail == result.getOrNull())
        coVerify { photoRepository.getPhotoDetail(photoId) }
    }

    @Test
    fun `Repository가 실패를 반환할 때 UseCase는 실패를 반환한다`() = runTest {
        // Given
        val photoId = "error-photo-id"
        val exception = Exception("Photo not found")
        val failureResult = Result.failure<PhotoDetail>(exception)

        coEvery { photoRepository.getPhotoDetail(photoId) } returns failureResult

        // When
        val result = getPhotoDetailUseCase(photoId)

        // Then
        assert(result.isFailure)
        assert(exception == result.exceptionOrNull())
        coVerify { photoRepository.getPhotoDetail(photoId) }
    }

    @Test
    fun `UseCase가 Repository에 올바른 사진 ID를 전달한다`() = runTest {
        // Given
        val photoId = "specific-photo-123"
        val successResult = Result.success(mockPhotoDetail)

        coEvery { photoRepository.getPhotoDetail(photoId) } returns successResult

        // When
        getPhotoDetailUseCase(photoId)

        // Then
        coVerify { photoRepository.getPhotoDetail(photoId) }
        confirmVerified(photoRepository)
    }

    @Test
    fun `UseCase가 Null 설명 이미지를 처리할 수 있다`() = runTest {
        // Given
        val photoId = "no-desc-id"
        val photoWithNullDesc = mockPhotoDetail.copy(description = null)
        val successResult = Result.success(photoWithNullDesc)

        coEvery { photoRepository.getPhotoDetail(photoId) } returns successResult

        // When
        val result = getPhotoDetailUseCase(photoId)

        // Then
        assert(result.isSuccess)
        assertNull(result.getOrNull()?.description)
    }

    @Test
    fun `UseCase는 Null 태그가 있는 이미지를 처리할 수 있다`() = runTest {
        // Given
        val photoId = "no-tags-id"
        val photoWithNullTags = mockPhotoDetail.copy(tags = null)
        val successResult = Result.success(photoWithNullTags)

        coEvery { photoRepository.getPhotoDetail(photoId) } returns successResult

        // When
        val result = getPhotoDetailUseCase(photoId)

        // Then
        assert(result.isSuccess)
        assertNull(result.getOrNull()?.tags)
    }

    @Test
    fun `UseCase는 서로 다른 Photo ID로 동작할 수 있다`() = runTest {
        // Given
        val photoIds = listOf("id-1", "id-2", "id-3", "special-id-123", "another_id")

        photoIds.forEach { photoId ->
            val photo = mockPhotoDetail.copy(id = photoId)
            val successResult = Result.success(photo)

            coEvery { photoRepository.getPhotoDetail(photoId) } returns successResult

            // When
            val result = getPhotoDetailUseCase(photoId)

            // Then
            assert(result.isSuccess)
            assert(photoId == result.getOrNull()?.id)
            coVerify { photoRepository.getPhotoDetail(photoId) }
        }
    }

    @Test
    fun `UseCase는 빈 문자열 ID를 받으면 에러를 반환한다`() = runTest {
        // Given
        val photoId = ""
        val exception = IllegalArgumentException("Photo ID cannot be empty")
        val failureResult = Result.failure<PhotoDetail>(exception)

        coEvery { photoRepository.getPhotoDetail(photoId) } returns failureResult

        // When
        val result = getPhotoDetailUseCase(photoId)

        // Then
        assert(result.isFailure)
    }

    @Test
    fun `UseCase는 서로 다른 ID로 여러 번 호출할 수 있다`() = runTest {
        // Given
        val photoId1 = "photo-1"
        val photoId2 = "photo-2"
        val photo1 = mockPhotoDetail.copy(id = photoId1)
        val photo2 = mockPhotoDetail.copy(id = photoId2)

        coEvery { photoRepository.getPhotoDetail(photoId1) } returns Result.success(photo1)
        coEvery { photoRepository.getPhotoDetail(photoId2) } returns Result.success(photo2)

        // When
        val result1 = getPhotoDetailUseCase(photoId1)
        val result2 = getPhotoDetailUseCase(photoId2)

        // Then
        assert(photoId1 == result1.getOrNull()?.id)
        assert(photoId2 == result2.getOrNull()?.id)
        coVerify { photoRepository.getPhotoDetail(photoId1) }
        coVerify { photoRepository.getPhotoDetail(photoId2) }
    }

    @Test
    fun `UseCase는 네트워크 타임아웃 예외를 처리한다`() = runTest {
        // Given
        val photoId = "timeout-id"
        val exception = java.net.SocketTimeoutException("Connection timeout")
        val failureResult = Result.failure<PhotoDetail>(exception)

        coEvery { photoRepository.getPhotoDetail(photoId) } returns failureResult

        // When
        val result = getPhotoDetailUseCase(photoId)

        // Then
        assert(result.isFailure)
        assert(result.exceptionOrNull() is java.net.SocketTimeoutException)
    }

    @Test
    fun `UseCase가 404 Not Found 시나리오를 처리한다`() = runTest {
        // Given
        val photoId = "non-existent-id"
        val exception = Exception("404 Not Found")
        val failureResult = Result.failure<PhotoDetail>(exception)

        coEvery { photoRepository.getPhotoDetail(photoId) } returns failureResult

        // When
        val result = getPhotoDetailUseCase(photoId)

        // Then
        assert(result.isFailure)
        assert(result.exceptionOrNull()?.message?.contains("404") == true)
    }
}