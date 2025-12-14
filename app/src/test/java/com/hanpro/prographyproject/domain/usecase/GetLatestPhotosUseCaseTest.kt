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
import org.junit.Before
import org.junit.Test

class GetLatestPhotosUseCaseTest {

    @MockK
    private lateinit var photoRepository: PhotoRepository
    private lateinit var getLatestPhotosUseCase: GetLatestPhotosUseCase

    private val mockPhotoDetail = PhotoDetail(
        id = "test-id",
        description = "Test Photo",
        urls = Urls(full = "https://example.com/photo.jpg", regular = "https://example.com/photo-regular.jpg"),
        tags = null,
        links = Link(download = "https://example.com/download"),
        user = User(username = "testuser")
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        getLatestPhotosUseCase = GetLatestPhotosUseCase(photoRepository)
    }

    @Test
    fun `Repository가 성공을 반환할 때 UseCase는 성공을 반환한다`() = runTest {
        // Given
        val page = 1
        val perPage = 30
        val mockPhotos = listOf(mockPhotoDetail)
        val successResult = Result.success(mockPhotos)

        coEvery { photoRepository.getLatestPhotos(page, perPage) } returns successResult

        // When
        val result = getLatestPhotosUseCase(page, perPage)

        // Then
        assert(result.isSuccess)
        assert(mockPhotos == result.getOrNull())
        coVerify { photoRepository.getLatestPhotos(page, perPage) }
    }

    @Test
    fun `Repository가 실패를 반환할 때 UseCase는 실패를 반환한다`() = runTest {
        // Given
        val page = 1
        val perPage = 30
        val exception = Exception("Repository error")
        val failureResult = Result.failure<List<PhotoDetail>>(exception)

        coEvery { photoRepository.getLatestPhotos(page, perPage) } returns failureResult

        // When
        val result = getLatestPhotosUseCase(page, perPage)

        // Then
        assert(result.isFailure)
        assert(exception == result.exceptionOrNull())
        coVerify { photoRepository.getLatestPhotos(page, perPage) }
    }

    @Test
    fun `UseCase는 Repository에 올바른 매개변수를 전달한다`() = runTest {
        // Given
        val page = 5
        val perPage = 50
        val successResult = Result.success(emptyList<PhotoDetail>())

        coEvery { photoRepository.getLatestPhotos(page, perPage) } returns successResult

        // When
        getLatestPhotosUseCase(page, perPage)

        // Then
        coVerify { photoRepository.getLatestPhotos(page, perPage) }
        confirmVerified(photoRepository)
    }

    @Test
    fun `UseCase가 빈 리스트 결과를 처리할 수 있다`() = runTest {
        // Given
        val page = 1
        val perPage = 30
        val successResult = Result.success(emptyList<PhotoDetail>())

        coEvery { photoRepository.getLatestPhotos(page, perPage) } returns successResult

        // When
        val result = getLatestPhotosUseCase(page, perPage)

        // Then
        assert(result.isSuccess)
        assert(emptyList<PhotoDetail>() == result.getOrNull())
        coVerify { photoRepository.getLatestPhotos(page, perPage) }
    }

    @Test
    fun `UseCase는 서로 다른 페이지 번호를 사용할 수 있다`() = runTest {
        // Given
        val pages = listOf(1, 2, 3, 10, 100)
        val perPage = 30

        pages.forEach { page ->
            val successResult = Result.success(emptyList<PhotoDetail>())

            coEvery { photoRepository.getLatestPhotos(page, perPage) } returns successResult

            // When
            val result = getLatestPhotosUseCase(page, perPage)

            // Then
            assert(result.isSuccess)
            assert(emptyList<PhotoDetail>() == result.getOrNull())
            coVerify { photoRepository.getLatestPhotos(page, perPage) }
        }
    }

    @Test
    fun `UseCase는 서로 다른 페이지 값을 사용할 수 있다`() = runTest {
        // Given
        val page = 1
        val perPageValues = listOf(10, 20, 30, 50, 100)

        perPageValues.forEach { perPage ->
            val successResult = Result.success(listOf(mockPhotoDetail))

            coEvery { photoRepository.getLatestPhotos(page, perPage) } returns successResult

            // When
            val result = getLatestPhotosUseCase(page, perPage)

            // Then
            assert(result.isSuccess)
            coVerify { photoRepository.getLatestPhotos(page, perPage) }
        }
    }

    @Test
    fun `UseCase는 대규모 사진 목록을 처리할 수 있다`() = runTest {
        // Given
        val page = 1
        val perPage = 100
        val largePhotoList = List(perPage) { mockPhotoDetail.copy(id = "photo-$it") }
        val successResult = Result.success(largePhotoList)

        coEvery { photoRepository.getLatestPhotos(page, perPage) } returns successResult

        // When
        val result = getLatestPhotosUseCase(page, perPage)

        // Then
        assert(result.isSuccess)
        assert(perPage == result.getOrNull()?.size)
        coVerify { photoRepository.getLatestPhotos(page, perPage) }
    }

    @Test
    fun `UseCase는 여러 번 올바르게 호출된다`() = runTest {
        // Given
        val page1 = 1
        val page2 = 2
        val perPage = 30
        val photos1 = listOf(mockPhotoDetail.copy(id = "1"))
        val photos2 = listOf(mockPhotoDetail.copy(id = "2"))

        coEvery { photoRepository.getLatestPhotos(page1, perPage) } returns Result.success(photos1)
        coEvery { photoRepository.getLatestPhotos(page2, perPage) } returns Result.success(photos2)

        // When
        val result1 = getLatestPhotosUseCase(page1, perPage)
        val result2 = getLatestPhotosUseCase(page2, perPage)

        // Then
        assert(photos1 == result1.getOrNull())
        assert(photos2 == result2.getOrNull())
        coVerify { photoRepository.getLatestPhotos(page1, perPage) }
        coVerify { photoRepository.getLatestPhotos(page2, perPage) }
    }
}