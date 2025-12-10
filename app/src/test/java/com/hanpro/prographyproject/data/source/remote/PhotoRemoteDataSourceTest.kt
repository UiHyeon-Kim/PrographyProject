package com.hanpro.prographyproject.data.source.remote

import com.hanpro.prographyproject.data.model.Link
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.data.model.Urls
import com.hanpro.prographyproject.data.model.User
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException

class PhotoRemoteDataSourceTest {

    // Mock(가짜) 객체 생성
    // 실제 네트워크 요청이 아닌, 특정 값만 반환하도록 설정해서 테스트 가능
    @MockK
    private lateinit var unsplashApiService: UnsplashApiService
    private lateinit var photoRemoteDataSource: PhotoRemoteDataSource

    // 테스트에서 사용할 가짜 응답 객체
    // 외부 API 호출하지 않아도 이 데이터를 반환하도록 Mock 설정하기 위함
    private val mockPhotoDetail = PhotoDetail(
        id = "test-id",
        description = "Test Photo",
        urls = Urls(full = "https://example.com/photo.jpg", regular = "https://example.com/photo-regular.jpg"),
        tags = emptyList(),
        links = Link(download = "https://example.com/download"),
        user = User(username = "testuser")
    )

    // @Test 어노테이션이 붙은 함수 실행 전 반드시 실행 - 테스트 초기화 설정
    @Before
    fun setup() {

        // @Mock 어노테이션이 붙은 객체들을 Mock 객체로 생성(초기화) - 여기선 unsplashApiService
        MockKAnnotations.init(this)

        // 테스트 대상 클래스 초기화 - Mock 서비스 주입
        photoRemoteDataSource = PhotoRemoteDataSource(unsplashApiService)
    }

    // getLatestPhotos 테스트

    // 하나의 테스트 케이스
    // runTest: Coroutine 테스트 지원 - suspend 함수를 테스트하기 위함
    @Test
    fun `getLatestPhotos는 API 호출이 성공하면 사진 목록과 함께 성공을 반환한다`() = runTest {
        // Given
        val page = 1
        val perPage = 10

        // API 응답으로 사용할 Mock 리스트
        val mockResponse = listOf(mockPhotoDetail, mockPhotoDetail.copy(id = "test-id-2"))

        // Mock API 가 특정 입력에 대해 어떤 값을 반환하게 할지 지정하는 부분
        // 네트워크 요청을 하지말고 mockResponse를 반환해라
        coEvery { unsplashApiService.getLatestPhotos(page, perPage) } returns mockResponse

        // When
        // 테스트 대상 함수 호출 - 내부에서 unsplashApiService.getPhotoPages() 가 호출됨(현재는 Mock 반환)
        val result = photoRemoteDataSource.getLatestPhotos(page, perPage)

        // Then
        assert(result.isSuccess) // 응답 성공했는지
        assert(mockResponse == result.getOrNull()) // result 값이 mockResponse 와 동일한지

        // verify: Mock 객체의 특정 메서드가 정확히 호출되었는지 확인. 시그니처까지 체크해 의도대로 동작했는지를 보장함
        // 이게 없으면 DataSource 내부에서 API를 호출 안해도 테스트 통과할 수 있음
        coVerify { unsplashApiService.getLatestPhotos(page, perPage) }
    }

    @Test
    fun `getLatestPhotos는 API가 빈 값을 반환할 때 빈 리스트로 성공을 반환한다`() = runTest {
        // Given
        val page = 1
        val perPage = 10

        coEvery { unsplashApiService.getLatestPhotos(page, perPage) } returns emptyList()

        // When
        val result = photoRemoteDataSource.getLatestPhotos(page, perPage)

        // Then
        assert(result.isSuccess)
        assert(emptyList<PhotoDetail>() == result.getOrNull())
        coVerify { unsplashApiService.getLatestPhotos(page, perPage) }
    }

    @Test
    fun `getLatestPhotos는 매개변수가 제공되지 않을 경우 기본 매개변수를 사용한다`() = runTest {
        // Given
        val mockPhotos = listOf(mockPhotoDetail)

        coEvery { unsplashApiService.getLatestPhotos() } returns mockPhotos

        // When
        val result = photoRemoteDataSource.getLatestPhotos()

        // Then
        assert(result.isSuccess)
        coVerify { unsplashApiService.getLatestPhotos(1, 30) }
    }

    @Test
    fun `getLatestPhotos는 API가 IOException을 던질 때 실패를 반환한다`() = runTest {
        // Given
        val page = 1
        val perPage = 10
        val exception = IOException("Network error")

        coEvery { unsplashApiService.getLatestPhotos(page, perPage) } throws exception

        // When
        val result = photoRemoteDataSource.getLatestPhotos(page, perPage)

        // Then
        assert(result.isFailure)
        assert(exception == result.exceptionOrNull())
        coEvery { unsplashApiService.getLatestPhotos(page, perPage) }
    }

    @Test
    fun `getLatestPhotos는 API가 generic exception을 던질 때 실패를 반환한다`() = runTest {
        // Given
        val page = 2
        val perPage = 20
        val exception = Exception("Generic error")

        coEvery { unsplashApiService.getLatestPhotos(page, perPage) } throws exception

        // When
        val result = photoRemoteDataSource.getLatestPhotos(page, perPage)

        // Then
        assert(result.isFailure)
        assert(exception == result.exceptionOrNull())
        coEvery { unsplashApiService.getLatestPhotos(page, perPage) }
    }

    @Test
    fun `getLatestPhotos는 다른 매개변수로 호출해도 정상 동작한다`() = runTest {
        // Given
        val page = 5
        val perPage = 50
        val mockPhotos = listOf(mockPhotoDetail)

        coEvery { unsplashApiService.getLatestPhotos(page, perPage) } returns mockPhotos

        // When
        val result = photoRemoteDataSource.getLatestPhotos(page, perPage)

        // Then
        assert(result.isSuccess)
        coVerify { unsplashApiService.getLatestPhotos(page, perPage) }
    }

    @Test
    fun `getLatestPhotos는 0페이지의 경계값을 처리한다`() = runTest {
        // Given
        val page = 0
        val perPage = 10
        val mockPhotos = listOf(mockPhotoDetail)

        coEvery { unsplashApiService.getLatestPhotos(page, perPage) } returns mockPhotos

        // When
        val result = photoRemoteDataSource.getLatestPhotos(page, perPage)

        // Then
        assert(result.isSuccess)
        coVerify { unsplashApiService.getLatestPhotos(page, perPage) }
    }


    // getRandomPhotos 테스트

    @Test
    fun `getRandomPhotos는 API 호출이 성공하면 사진 리스트와 함께 성공을 반환한다`() = runTest {
        // Given
        val count = 10
        val mockPhotos = listOf(mockPhotoDetail, mockPhotoDetail.copy(id = "random-id"))

        coEvery { unsplashApiService.getRandomPhotos(count) } returns mockPhotos

        // When
        val result = photoRemoteDataSource.getRandomPhotos(count)

        // Then
        assert(result.isSuccess)
        assert(mockPhotos == result.getOrNull())
        coVerify { unsplashApiService.getRandomPhotos(count) }
    }

    @Test
    fun `getRandomPhotos는 매개변수가 제공되지 않을 경우 기본 값을 사용한다`() = runTest {
        // Given
        val mockPhotos = listOf(mockPhotoDetail)

        coEvery { unsplashApiService.getRandomPhotos() } returns mockPhotos

        // When
        val result = photoRemoteDataSource.getRandomPhotos()

        // Then
        assert(result.isSuccess)
        coVerify { unsplashApiService.getRandomPhotos() }
    }

    @Test
    fun `getRandomPhotos는 API가 빈 경우 빈 리스트로 성공을 반환한다`() = runTest {
        // Given
        val count = 10

        coEvery { unsplashApiService.getRandomPhotos(count) } returns emptyList()

        // When
        val result = photoRemoteDataSource.getRandomPhotos(count)

        // Then
        assert(result.isSuccess)
        assert(emptyList<PhotoDetail>() == result.getOrNull())
        coVerify { unsplashApiService.getRandomPhotos(count) }
    }

    @Test
    fun `getRandomPhotos는 API가 IOException을 던질 때 실패를 반환한다`() = runTest {
        // Given
        val count = 10
        val exception = IOException("Network error")

        coEvery { unsplashApiService.getRandomPhotos(count) } throws exception

        // When
        val result = photoRemoteDataSource.getRandomPhotos(count)

        // Then
        assert(result.isFailure)
        assert(exception == result.exceptionOrNull())
        coEvery { unsplashApiService.getRandomPhotos(count) }
    }

    @Test
    fun `getRandomPhotos는 다른 카운트 값을 사용해도 정상 동작한다`() = runTest {
        // Given
        val count = 20
        val mockPhotos = List(count) { mockPhotoDetail.copy(id = "random-id-$it") }

        coEvery { unsplashApiService.getRandomPhotos(count) } returns mockPhotos

        // When
        val result = photoRemoteDataSource.getRandomPhotos(count)

        // Then
        assert(result.isSuccess)
        assert(count == result.getOrNull()?.size)
        coVerify { unsplashApiService.getRandomPhotos(count) }
    }

    @Test
    fun `getRandomPhotos는 1개의 이미지를 요청할 수 있다`() = runTest {
        // Given
        val count = 1
        val mockPhotos = listOf(mockPhotoDetail)

        coEvery { unsplashApiService.getRandomPhotos(count) } returns mockPhotos

        // When
        val result = photoRemoteDataSource.getRandomPhotos(count)

        // Then
        assert(result.isSuccess)
        assert(mockPhotos == result.getOrNull())
        coVerify { unsplashApiService.getRandomPhotos(count) }
    }

    @Test
    fun `getRandomPhotos는 큰 카운트 값을 처리할 수 있다`() = runTest {
        val count = 100
        val mockPhotos = List(count) { mockPhotoDetail.copy(id = "random-id-$it") }

        coEvery { unsplashApiService.getRandomPhotos(count) } returns mockPhotos

        // When
        val result = photoRemoteDataSource.getRandomPhotos(count)

        // Then
        assert(result.isSuccess)
        assert(count == result.getOrNull()?.size)
        coVerify { unsplashApiService.getRandomPhotos(count) }
    }


    // getPhotoDetail 테스트

    @Test
    fun `getPhotoDetail은 API 호출이 성공하면 photo detail과 함께 성공을 반환한다`() = runTest {
        // Given
        val photoId = "test-id"

        coEvery { unsplashApiService.getPhotoDetail(photoId) } returns mockPhotoDetail

        // When
        val result = photoRemoteDataSource.getPhotoDetail(photoId)

        // Then
        assert(result.isSuccess)
        assert(mockPhotoDetail == result.getOrNull())
        coVerify { unsplashApiService.getPhotoDetail(photoId) }
    }

    @Test
    fun `getPhotoDetail은 API가 IOException을 던질 때 실패를 반환한다`() = runTest {
        // Given
        val photoId = "test-id"
        val exception = IOException("Network error")

        coEvery { unsplashApiService.getPhotoDetail(photoId) } throws exception

        // When
        val result = photoRemoteDataSource.getPhotoDetail(photoId)

        // Then
        assert(result.isFailure)
        assert(exception == result.exceptionOrNull())
        coEvery { unsplashApiService.getPhotoDetail(photoId) }
    }

    @Test
    fun `getPhotoDetail은 사진을 찾지 못하면 실패를 반환한다`() = runTest {
        // Given
        val photoId = "non-existent-id"
        val exception = RuntimeException("404 Not Found")

        coEvery { unsplashApiService.getPhotoDetail(photoId) } throws exception

        // When
        val result = photoRemoteDataSource.getPhotoDetail(photoId)

        // Then
        assert(result.isFailure)
        assert(exception == result.exceptionOrNull())
        coEvery { unsplashApiService.getPhotoDetail(photoId) }
    }

    @Test
    fun `getPhotoDetail은 빈 문자열 ID일 때 오류를 반환한다`() = runTest {
        // Given
        val photoId = ""
        val exception = IllegalArgumentException("Invalid photo ID")

        coEvery { unsplashApiService.getPhotoDetail(photoId) } throws exception

        // When
        val result = photoRemoteDataSource.getPhotoDetail(photoId)

        // Then
        assert(result.isFailure)
        assert(exception == result.exceptionOrNull())
        coEvery { unsplashApiService.getPhotoDetail(photoId) }
    }

    @Test
    fun `getPhotoDetail은 ID에서 특수 문자를 다룰 수 있다`() = runTest {
        // Given
        val photoId = "photo-id-with-special-chars-123_abc@"

        coEvery { unsplashApiService.getPhotoDetail(photoId) } returns mockPhotoDetail

        // When
        val result = photoRemoteDataSource.getPhotoDetail(photoId)

        // Then
        assert(result.isSuccess)
        assert(mockPhotoDetail == result.getOrNull())
        coVerify { unsplashApiService.getPhotoDetail(photoId) }
    }

    @Test
    fun `getPhotoDetail은 응답 시 null description을 처리할 수 있다`() = runTest {
        // Given
        val photoId = "test-id"
        val mockPhotoDetailWithNullDescription = mockPhotoDetail.copy(description = null)

        coEvery { unsplashApiService.getPhotoDetail(photoId) } returns mockPhotoDetailWithNullDescription

        // When
        val result = photoRemoteDataSource.getPhotoDetail(photoId)

        // Then
        assert(result.isSuccess)
        assert(mockPhotoDetailWithNullDescription == result.getOrNull())
        coVerify { unsplashApiService.getPhotoDetail(photoId) }
    }

    @Test
    fun `getPhotoDetail은 null 태그를 처리할 수 있다`() = runTest {
        // Given
        val photoId = "test-id"
        val mockPhotoDetailWithNullTags = mockPhotoDetail.copy(tags = null)

        coEvery { unsplashApiService.getPhotoDetail(photoId) } returns mockPhotoDetailWithNullTags

        // When
        val result = photoRemoteDataSource.getPhotoDetail(photoId)

        // Then
        assert(result.isSuccess)
        assert(mockPhotoDetailWithNullTags == result.getOrNull())
        coVerify { unsplashApiService.getPhotoDetail(photoId) }
    }
}