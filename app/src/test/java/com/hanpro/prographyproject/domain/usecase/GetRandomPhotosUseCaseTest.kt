package com.hanpro.prographyproject.domain.usecase

import com.hanpro.prographyproject.data.model.Link
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.data.model.Urls
import com.hanpro.prographyproject.data.model.User
import com.hanpro.prographyproject.domain.repository.PhotoRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetRandomPhotosUseCaseTest {

    @MockK
    private lateinit var photoRepository: PhotoRepository
    private lateinit var getRandomPhotosUseCase: GetRandomPhotosUseCase

    private val mockPhotoDetail = PhotoDetail(
        id = "random-usecase-id",
        description = "Random UseCase test photo",
        urls = Urls(full = "https://example.com/full.jpg", regular = "https://example.com/regular.jpg"),
        tags = null,
        links = Link(download = "https://example.com/download"),
        user = User(username = "randomuser")
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        getRandomPhotosUseCase = GetRandomPhotosUseCase(photoRepository)
    }

    @Test
    fun `Repository가 성공을 반환할 때 UseCase는 성공을 반환한다`() = runTest {
        // Given
        val count = 10
        val mockPhotos = listOf(mockPhotoDetail, mockPhotoDetail.copy(id = "random-2"))
        val successResult = Result.success(mockPhotos)

        coEvery { photoRepository.getRandomPhotos(count) } returns successResult

        // When
        val result = getRandomPhotosUseCase(count)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockPhotos, result.getOrNull())
        coVerify { photoRepository.getRandomPhotos(count) }
    }

    @Test
    fun `Repository가 실패를 반환할 때 UseCase는 실패를 반환한다`() = runTest {
        // Given
        val count = 10
        val exception = Exception("Random photos error")
        val failureResult = Result.failure<List<PhotoDetail>>(exception)

        coEvery { photoRepository.getRandomPhotos(count) } returns failureResult

        // When
        val result = getRandomPhotosUseCase(count)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify { photoRepository.getRandomPhotos(count) }
    }

    @Test
    fun `UseCase는 Repository에 올바른 카운트를 전달한다`() = runTest {
        // Given
        val count = 20
        val successResult = Result.success(emptyList<PhotoDetail>())

        coEvery { photoRepository.getRandomPhotos(count) } returns successResult

        // When
        getRandomPhotosUseCase(count)

        // Then
        coVerify { photoRepository.getRandomPhotos(count) }
        confirmVerified(photoRepository)
    }

    @Test
    fun `UseCase는 빈 리스트 결과를 처리한다`() = runTest {
        // Given
        val count = 10
        val successResult = Result.success(emptyList<PhotoDetail>())

        coEvery { photoRepository.getRandomPhotos(count) } returns successResult

        // When
        val result = getRandomPhotosUseCase(count)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }

    @Test
    fun `UseCase는 다양한 카운트 값을 사용한다`() = runTest {
        // Given
        val counts = listOf(1, 5, 10, 20, 50)

        counts.forEach { count ->
            val photos = List(count) { mockPhotoDetail.copy(id = "random-$it") }
            val successResult = Result.success(photos)

            coEvery { photoRepository.getRandomPhotos(count) } returns successResult

            // When
            val result = getRandomPhotosUseCase(count)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(count, result.getOrNull()?.size)
            coVerify { photoRepository.getRandomPhotos(count) }
        }
    }

    @Test
    fun `UseCase는 하나의 사진을 처리한다`() = runTest {
        // Given
        val count = 1
        val singlePhoto = listOf(mockPhotoDetail)
        val successResult = Result.success(singlePhoto)

        coEvery { photoRepository.getRandomPhotos(count) } returns successResult

        // When
        val result = getRandomPhotosUseCase(count)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(count, result.getOrNull()?.size)
    }

    @Test
    fun `UseCase는 큰 카운트 값을 처리한다`() = runTest {
        // Given
        val count = 100
        val largePhotoList = List(count) { mockPhotoDetail.copy(id = "large-$it") }
        val successResult = Result.success(largePhotoList)

        coEvery { photoRepository.getRandomPhotos(count) } returns successResult

        // When
        val result = getRandomPhotosUseCase(count)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(count, result.getOrNull()?.size)
    }

    @Test
    fun `UseCase는 다른 횟수로 여러 번 호출된다`() = runTest {
        // Given
        val count1 = 10
        val count2 = 20
        val photos1 = List(count1) { mockPhotoDetail.copy(id = "batch1-$it") }
        val photos2 = List(count2) { mockPhotoDetail.copy(id = "batch2-$it") }

        coEvery { photoRepository.getRandomPhotos(count1) } returns Result.success(photos1)
        coEvery { photoRepository.getRandomPhotos(count2) } returns Result.success(photos2)

        // When
        val result1 = getRandomPhotosUseCase(count1)
        val result2 = getRandomPhotosUseCase(count2)

        // Then
        assertEquals(count1, result1.getOrNull()?.size)
        assertEquals(count2, result2.getOrNull()?.size)
        coVerify { photoRepository.getRandomPhotos(count1) }
        coVerify { photoRepository.getRandomPhotos(count2) }
    }

    @Test
    fun `UseCase는 여러번 호출에서 서로 다른 무작위 사진을 반환한다`() = runTest {
        // Given
        val count = 10
        val firstBatch = List(count) { mockPhotoDetail.copy(id = "first-$it") }
        val secondBatch = List(count) { mockPhotoDetail.copy(id = "second-$it") }

        coEvery { photoRepository.getRandomPhotos(count) } returns Result.success(firstBatch) andThen Result.success(
            secondBatch
        )

        // When
        val result1 = getRandomPhotosUseCase(count)
        val result2 = getRandomPhotosUseCase(count)

        // Then
        assertNotEquals(result1.getOrNull(), result2.getOrNull())
        coVerify(exactly = 2) { photoRepository.getRandomPhotos(count) }
    }
}