package com.hanpro.prographyproject.ui.viewmodel

import com.hanpro.prographyproject.common.utils.NetworkEvent
import com.hanpro.prographyproject.common.utils.NetworkManager
import com.hanpro.prographyproject.data.model.Link
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.data.model.Urls
import com.hanpro.prographyproject.data.model.User
import com.hanpro.prographyproject.data.source.local.Bookmark
import com.hanpro.prographyproject.domain.usecase.*
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoViewModelTest {

    @MockK
    private lateinit var getLatestPhotosUseCase: GetLatestPhotosUseCase

    @MockK
    private lateinit var getRandomPhotosUseCase: GetRandomPhotosUseCase

    @MockK
    private lateinit var getBookmarksUseCase: GetBookmarksUseCase

    @MockK
    private lateinit var addBookmarkUseCase: AddBookmarkUseCase

    @MockK
    private lateinit var deleteBookmarkUseCase: DeleteBookmarkUseCase

    @MockK
    private lateinit var networkManager: NetworkManager

    private lateinit var viewModel: PhotoViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val mockPhotoDetail = PhotoDetail(
        id = "photo-1",
        description = "Test photo",
        urls = Urls(full = "https://example.com/full.jpg", regular = "https://example.com/regular.jpg"),
        tags = null,
        links = Link(download = "https://example.com/download"),
        user = User(username = "testuser")
    )

    private val mockBookmark = Bookmark(
        id = "photo-1",
        description = "Test photo",
        imageUrl = "https://example.com/regular.jpg"
    )

    private val networkStateFlow = MutableStateFlow(true)
    private val networkEventFlow = MutableStateFlow<NetworkEvent?>(null)

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // network manager mock 설정
        coEvery { networkManager.networkState } returns networkStateFlow
        coEvery { networkManager.networkEvent } returns networkEventFlow
        coEvery { networkManager.checkNetworkConnection() } returns true

        // 기본 UseCase 반환 설정
        // ViewModel 내의 init 함수로 인해 값이 들어가 있을 수 있어 빈값으로 해두어야 함
        coEvery { getBookmarksUseCase() } returns flowOf(emptyList())
        coEvery { getLatestPhotosUseCase(any(), any()) } returns Result.success(emptyList())
        coEvery { getRandomPhotosUseCase(any()) } returns Result.success(emptyList())
        every { networkManager.clearEvent() } just Runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): PhotoViewModel {
        return PhotoViewModel(
            getLatestPhotosUseCase,
            getRandomPhotosUseCase,
            getBookmarksUseCase,
            addBookmarkUseCase,
            deleteBookmarkUseCase,
            networkManager
        )
    }

    // 초기 상태 테스트

    @Test
    fun `초기 상태는 올바르게 설정되어 있다`() {
        // When
        viewModel = createViewModel()

        // Then
        val state = viewModel.uiState.value
        assert(state.bookmarks.isEmpty())
        assert(state.photos.isEmpty())
        assert(state.randomPhotos.isEmpty())
        assert(0 == state.randomPhotoIndex)
        assert(state.isLoading)
        assertNull(state.error)
    }


    // loadLatestPhotos 테스트

    @Test
    fun `loadLatestPhotos가 성공하면 상태를 업데이트한다`() = runTest {
        // Given
        val photos = listOf(mockPhotoDetail, mockPhotoDetail.copy(id = "photo-2"))

        coEvery { getLatestPhotosUseCase(1, 30) } returns Result.success(photos)

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.loadLatestPhotos(1, 30)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assert(photos == state.photos)
        assert(!state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `loadLatestPhotos가 실패하면 오류 상태를 설정한다`() = runTest {
        // Given
        val errorMessage = "Network error"

        coEvery { getLatestPhotosUseCase(1, 30) } returns Result.failure(Exception(errorMessage))

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.loadLatestPhotos(1, 30)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assert(state.photos.isEmpty())
        assert(!state.isLoading)
        assert(errorMessage == state.error)
    }

    @Test
    fun `loadLatestPhotos는 페이지가 1보다 클 때 사진을 덧붙인다`() = runTest {
        // Given
        val page1Photos = listOf(mockPhotoDetail.copy(id = "photo-1"))
        val page2Photos = listOf(mockPhotoDetail.copy(id = "photo-2"))

        coEvery { getLatestPhotosUseCase(1, 30) } returns Result.success(page1Photos)
        coEvery { getLatestPhotosUseCase(2, 30) } returns Result.success(page2Photos)

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.loadLatestPhotos(1, 30)
        advanceUntilIdle()
        viewModel.loadLatestPhotos(2, 30)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assert(2 == state.photos.size)
        assert(state.photos.any { it.id == "photo-1" })
        assert(state.photos.any { it.id == "photo-2" })
    }

    @Test
    fun `loadLatestPhotos가 1페이지에 있을 때 사진을 교체한다`() = runTest {
        // Given
        val firstPhotos = listOf(mockPhotoDetail.copy(id = "old-1"))
        val newPhotos = listOf(mockPhotoDetail.copy(id = "new-1"))

        coEvery { getLatestPhotosUseCase(1, 30) } returns Result.success(firstPhotos)

        viewModel = createViewModel()
        viewModel.loadLatestPhotos(1, 30)
        advanceUntilIdle()

        // When
        coEvery { getLatestPhotosUseCase(1, 30) } returns Result.success(newPhotos)
        viewModel.loadLatestPhotos(1, 30)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assert(1 == state.photos.size)
        assert("new-1" == state.photos[0].id)
    }

    @Test
    fun `loadLatestPhotos는 덧붙일 때 중복 파일을 제거한다`() = runTest {
        // Given
        val page1Photos = listOf(mockPhotoDetail.copy(id = "photo-1"))
        val page2Photos = listOf(
            mockPhotoDetail.copy(id = "photo-1"), // duplicate
            mockPhotoDetail.copy(id = "photo-2")
        )

        coEvery { getLatestPhotosUseCase(1, 30) } returns Result.success(page1Photos)
        coEvery { getLatestPhotosUseCase(2, 30) } returns Result.success(page2Photos)

        viewModel = createViewModel()

        // When
        viewModel.loadLatestPhotos(1, 30)
        advanceUntilIdle()
        viewModel.loadLatestPhotos(2, 30)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assert(2 == state.photos.size)
        assert(listOf("photo-1", "photo-2") == state.photos.map { it.id })
    }

    @Test
    fun `loadLatestPhotos는 네트워크가 끊겼을 때 API를 호출하지 않는다`() = runTest {
        // Given
        networkStateFlow.value = false
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.loadLatestPhotos(1, 30)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { getLatestPhotosUseCase.invoke(any(), any()) }

        val state = viewModel.uiState.value
        assert(!state.isLoading)
        assert("네트워크 연결이 필요합니다." == state.error)
    }


    // loadRandomPhotos 테스트

    @Test
    fun `loadRandomPhotos가 성공하면 사진을 추가한다`() = runTest {
        // Given
        val randomPhotos = listOf(mockPhotoDetail.copy(id = "random-1"))

        coEvery { getRandomPhotosUseCase(10) } returns Result.success(randomPhotos)

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.loadRandomPhotos()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assert(state.randomPhotos.contains(mockPhotoDetail.copy(id = "random-1")))
        assert(!state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `loadRandomPhotos는 기존의 무작위 사진에 덧붙인다`() = runTest {
        // Given
        val batch1 = listOf(mockPhotoDetail.copy(id = "random-1"))
        val batch2 = listOf(mockPhotoDetail.copy(id = "random-2"))

        coEvery { getRandomPhotosUseCase(10) } returns Result.success(batch1)

        viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.loadRandomPhotos()
        advanceUntilIdle()

        // When
        coEvery { getRandomPhotosUseCase(10) } returns Result.success(batch2)
        viewModel.loadRandomPhotos()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assert(2 <= state.randomPhotos.size)
    }

    @Test
    fun `loadRandomPhotos가 실패하면 오류 상태를 설정한다`() = runTest {
        // Given
        val errorMessage = "Random photos error"

        coEvery { getRandomPhotosUseCase(10) } returns Result.failure(Exception(errorMessage))
        viewModel = createViewModel()

        // When
        viewModel.loadRandomPhotos()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assert(state.randomPhotos.isEmpty())
        assert(!state.isLoading)
        assert(errorMessage == state.error)
    }

    @Test
    fun `loadRandomPhotos는 네트워크가 끊겼을 때 API를 호출하지 않는다`() = runTest {
        // Given
        networkStateFlow.value = false
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.loadRandomPhotos()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { getRandomPhotosUseCase.invoke(any()) }

        val state = viewModel.uiState.value
        assert(!state.isLoading)
        assert("네트워크 연결이 필요합니다." == state.error)
    }


    // Bookmark 테스트

    @Test
    fun `addBookmark는 UseCase를 호출한다`() = runTest {
        // Given
        coEvery { addBookmarkUseCase(any()) } returns Unit
        viewModel = createViewModel()

        // When
        viewModel.addBookmark(mockPhotoDetail)
        advanceUntilIdle()

        // Then
        coVerify { addBookmarkUseCase.invoke(any()) }
    }

    @Test
    fun `deleteBookmarksms UseCase를 호출한다`() = runTest {
        // Given
        coEvery { deleteBookmarkUseCase(any()) } returns Unit
        viewModel = createViewModel()

        // When
        viewModel.deleteBookmark(mockPhotoDetail)
        advanceUntilIdle()

        // Then
        coVerify { deleteBookmarkUseCase.invoke(any()) }
    }

    @Test
    fun `isBookmarked는 사진이 북마크되면 true를 반환한다`() = runTest {
        // Given
        val bookmarks = listOf(mockBookmark)

        coEvery { getBookmarksUseCase() } returns flowOf(bookmarks)
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        val isBookmarked = viewModel.isBookmarked("photo-1")

        // Then
        assert(isBookmarked)
    }

    @Test
    fun `isBookmark는 사진이 북마크되지 않은 경우 false를 반환한다`() = runTest {
        // Given
        coEvery { getBookmarksUseCase() } returns flowOf(emptyList())
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        val isBookmarked = viewModel.isBookmarked("photo-1")

        // Then
        assert(!isBookmarked)
    }


    // 이미지 인덱스 테스트

    @Test
    fun `incrementIndex는 randomPhotoIndex를 1만큼 증가시킨다`() = runTest {
        // Given
        viewModel = createViewModel()
        assert(0 == viewModel.uiState.value.randomPhotoIndex)

        // When
        viewModel.incrementIndex()
        advanceUntilIdle()

        // Then
        assert(1 == viewModel.uiState.value.randomPhotoIndex)
    }

    @Test
    fun `incrementIndex는 여러 번 호출할 수 있다`() = runTest {
        // Given
        viewModel = createViewModel()

        // When
        repeat(5) {
            viewModel.incrementIndex()
        }
        advanceUntilIdle()

        // Then
        assert(5 == viewModel.uiState.value.randomPhotoIndex)
    }

    // NetworkEvent 테스트

    @Test
    fun `onNetworkEventShown은 네트워크 이벤트를 클리어한다`() = runTest {
        // Given
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.onNetworkEventShown()

        // Then
        verify { networkManager.clearEvent() }
    }

    @Test
    fun `retryConnection은 네트워크를 확인하고 연결 시 사진을 불러온다`() = runTest {
        // Given
        coEvery { networkManager.checkNetworkConnection() } returns true
        coEvery { getLatestPhotosUseCase(1, 30) } returns Result.success(emptyList())
        coEvery { getRandomPhotosUseCase(10) } returns Result.success(emptyList())

        viewModel = createViewModel()

        // When
        viewModel.retryConnection()
        advanceUntilIdle()

        // Then
        coVerify { networkManager.checkNetworkConnection() }
    }

    @Test
    fun `retryConnection이 네트워크 확인에 실패하면 사진이 불러오지 않는다`() = runTest {
        // Given
        coEvery { networkManager.checkNetworkConnection() } returns false
        viewModel = createViewModel()

        // When
        viewModel.retryConnection()
        advanceUntilIdle()

        // Then
        coVerify { networkManager.checkNetworkConnection() }
    }


    // 예외 사례 및 오류 처리

    @Test
    fun `loadLatestPhotos는 사용자 지정 페이지 값과 페이지당 값으로 호출될 수 있다`() = runTest {
        // Given
        val page = 5
        val perPage = 50
        val photos = listOf(mockPhotoDetail)

        coEvery { getLatestPhotosUseCase(page, perPage) } returns Result.success(photos)
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.loadLatestPhotos(page, perPage)
        advanceUntilIdle()

        // Then
        coVerify { getLatestPhotosUseCase(page, perPage) }
        assert(photos == viewModel.uiState.value.photos)
    }

    @Test
    fun `loadLatestPhotos는 빈 응답을 처리한다`() = runTest {
        // Given
        coEvery { getLatestPhotosUseCase(1, 30) } returns Result.success(emptyList())
        viewModel = createViewModel()

        // When
        viewModel.loadLatestPhotos(1, 30)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assert(state.photos.isEmpty())
        assert(!state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `loadRandomPhotos는 빈 응답을 처리한다`() = runTest {
        // Given
        coEvery { getRandomPhotosUseCase(10) } returns Result.success(emptyList())
        viewModel = createViewModel()

        // When
        viewModel.loadRandomPhotos()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assert(state.randomPhotos.isEmpty())
        assert(!state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `북마크는 상태에서 관찰되고 업데이트된다`() = runTest {
        // Given
        val bookmarksFlow = MutableStateFlow(emptyList<Bookmark>())

        coEvery { getBookmarksUseCase() } returns bookmarksFlow
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        bookmarksFlow.value = listOf(mockBookmark)
        advanceUntilIdle()

        // Then
        assert(1 == viewModel.uiState.value.bookmarks.size)
        assert(mockBookmark == viewModel.uiState.value.bookmarks[0])
    }

    @Test
    fun `여러 연산이 올바른 상태를 유지한다`() = runTest {
        // Given
        val photos = listOf(mockPhotoDetail)
        val randomPhotos = listOf(mockPhotoDetail.copy(id = "random-1"))
        val bookmarks = listOf(mockBookmark)

        coEvery { getLatestPhotosUseCase(1, 30) } returns Result.success(photos)
        coEvery { getRandomPhotosUseCase(10) } returns Result.success(randomPhotos)
        coEvery { getBookmarksUseCase() } returns flowOf(bookmarks)
        coEvery { addBookmarkUseCase(any()) } returns Unit

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.loadLatestPhotos(1, 30)
        advanceUntilIdle()
        viewModel.loadRandomPhotos()
        advanceUntilIdle()
        viewModel.addBookmark(mockPhotoDetail)
        advanceUntilIdle()
        viewModel.incrementIndex()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assert(photos == state.photos)
        assert(state.randomPhotos.any { it.id == "random-1" })
    }
}