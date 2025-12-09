package com.hanpro.prographyproject.data.source.remote

import com.hanpro.prographyproject.data.model.Link
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.data.model.Urls
import com.hanpro.prographyproject.data.model.User
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class PhotoRemoteDataSourceTest {

    // Mock(가짜) 객체 생성
    // 실제 네트워크 요청이 아닌, 특정 값만 반환하도록 설정해서 테스트 가능
    @Mock
    private lateinit var unsplashApiService: UnsplashApiService
    private lateinit var photoRemoteDataSource: PhotoRemoteDataSource

    // 테스트에서 사용할 가짜 응답 객체
    // 외부 API 호출하지 않아도 이 데이터를 반환하도록 Mock 설정하기 위함
    private val mockPhotoDetail = PhotoDetail(
        id = "123",
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
        MockitoAnnotations.openMocks(this)

        // 테스트 대상 클래스 초기화 - Mock 서비스 주입
        photoRemoteDataSource = PhotoRemoteDataSource(unsplashApiService)
    }

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
        `when`(unsplashApiService.getLatestPhotos(page, perPage)).thenReturn(mockResponse)

        // When
        // 테스트 대상 함수 호출 - 내부에서 unsplashApiService.getPhotoPages() 가 호출됨(현재는 Mock 반환)
        val result = photoRemoteDataSource.getLatestPhotos(page, perPage)

        // Then
        assert(result.isSuccess) // 응답 성공했는지
        assert(mockResponse == result.getOrNull()) // result 값이 mockResponse 와 동일한지

        // verify: Mock 객체의 특정 메서드가 정확히 호출되었는지 확인. 시그니처까지 체크해 의도대로 동작했는지를 보장함
        // 이게 없으면 DataSource 내부에서 API를 호출 안해도 테스트 통과할 수 있음
        verify(unsplashApiService).getLatestPhotos(page, perPage)
    }
}