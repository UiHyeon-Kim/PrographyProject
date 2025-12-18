package com.hanpro.prographyproject.ui.viewmodel

import com.hanpro.prographyproject.data.model.Link
import com.hanpro.prographyproject.data.model.PhotoDetail
import com.hanpro.prographyproject.data.model.Urls
import com.hanpro.prographyproject.data.model.User
import com.hanpro.prographyproject.data.source.local.Bookmark
import com.hanpro.prographyproject.domain.usecase.AddBookmarkUseCase
import com.hanpro.prographyproject.domain.usecase.DeleteBookmarkUseCase
import com.hanpro.prographyproject.domain.usecase.GetBookmarksUseCase
import com.hanpro.prographyproject.domain.usecase.GetPhotoDetailUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// @OptIn: 실험적인 API를 사용하겠다고 컴파일러에게 알림
// 여기선 코루틴 테스트 API 중 일부가 아직 안정화가 안되어 작성이 필요 .setMain / .resetMain 등
@OptIn(ExperimentalCoroutinesApi::class)
class PhotoDetailViewModelTest {

    @MockK
    private lateinit var getPhotoDetailUseCase: GetPhotoDetailUseCase

    @MockK
    private lateinit var getBookmarksUseCase: GetBookmarksUseCase

    @MockK
    private lateinit var addBookmarkUseCase: AddBookmarkUseCase

    @MockK
    private lateinit var deleteBookmarkUseCase: DeleteBookmarkUseCase

    private lateinit var viewModel: PhotoDetailViewModel

    // StandardTestDispatcher: 코루틴 테스트용 디스패처
    // runTest와 조합해 수동으로 시점 제어(advanceUntilIdle 등)를 할 수 있게 해줌
    // 기본 test dispatcher와 다르게 자동 진행되지 않고 명시적 제어 가능
    private val testDispatcher = StandardTestDispatcher()

    private val mockPhotoDetail = PhotoDetail(
        id = "test-photo-id",
        description = "Test photo description",
        urls = Urls(full = "https://example.com/full.jpg", regular = "https://example.com/regular.jpg"),
        tags = null,
        links = Link(download = "https://example.com/download"),
        user = User(username = "testuser")
    )

    private val mockBookmark = Bookmark(
        id = "test-photo-id",
        description = "Test photo description",
        imageUrl = "https://example.com/regular.jpg"
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        // 코루틴의 Main 디스패처를 테스트용 디스패처로 변경
        // ViewModel에서 Dispatchers.main을 사용하는 코드(viewModelScope 등)가 테스트 환경에서 동작하도록 함
        Dispatchers.setMain(testDispatcher)
        viewModel = PhotoDetailViewModel(
            getPhotoDetailUseCase,
            getBookmarksUseCase,
            addBookmarkUseCase,
            deleteBookmarkUseCase
        )
    }

    @After
    fun tearDown() {
        // 테스트가 끝난 뒤 Main 디스패처를 원래대로 복원
        Dispatchers.resetMain()
    }

    // LoadPhotoDetail 테스트

    // runTest는 자체 test scheduler를 사용하지만, setMain으로 Main을 testDispatcher로 설정함
    // runTest 내부에서 디스패처를 제어하려면 StandardTestDispatcher 같은 수동형 디스패처를 사용하는 것이 안전
    @Test
    fun `loadPhotoDetail은 처음에 로딩 상태를 true로 설정한다`() = runTest {
        // Given
        val photoId = "test-photo-id"

        coEvery { getPhotoDetailUseCase(photoId) } returns Result.success(mockPhotoDetail)
        coEvery { getBookmarksUseCase() } returns flowOf(listOf(mockBookmark))

        // When
        viewModel.loadPhotoDetail(photoId)

        // Then
        assertTrue(viewModel.uiState.value.isLoading)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
        // testDispatcher를 전진시켜 모든 scheduled coroutine 작업이 완료될 때까지 진행
        // StandardTestDispatcher를 쓸 땐 자동 진행이 안되므로 명시적 호출 필요
        // UnconfinedTestDispatcher 등 자동 진행되는 디스패처면 필요 없을 수도 있음
        coVerify { getPhotoDetailUseCase(photoId) }
    }

    @Test
    fun `loadPhotoDetail은 성공하면 사진과 상태를 업데이트하고 북마크되지 않음을 보여준다`() = runTest {
        // Given
        val photoId = "test-photo-id"

        coEvery { getPhotoDetailUseCase(photoId) } returns Result.success(mockPhotoDetail)
        coEvery { getBookmarksUseCase() } returns flowOf(emptyList())

        // When
        viewModel.loadPhotoDetail(photoId)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(state.photo, mockPhotoDetail)
        assertFalse(state.isLoading)
        assertFalse(state.isBookmarked)
        assertNull(state.error)
    }

    @Test
    fun `loadPhotoDetail은 사진이 북마크 상태일 때 사진과 상태를 업데이트하고 북마크됨을 보여준다`() = runTest {
        // Given
        val photoId = "test-photo-id"
        val bookmarks = listOf(mockBookmark)

        coEvery { getPhotoDetailUseCase(photoId) } returns Result.success(mockPhotoDetail)
        coEvery { getBookmarksUseCase() } returns flowOf(bookmarks)

        // When
        viewModel.loadPhotoDetail(photoId)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(state.photo, mockPhotoDetail)
        assertFalse(state.isLoading)
        assertTrue(state.isBookmarked)
        assertNull(state.error)
    }

    @Test
    fun `loadPhotoDetail은 UseCase가 실패할 때 오류 상태를 설정한다`() = runTest {
        // Given
        val photoId = "test-photo-id"
        val errorMessage = "Network error"

        coEvery { getPhotoDetailUseCase(photoId) } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.loadPhotoDetail(photoId)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertNull(state.photo)
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
        assertFalse(state.isBookmarked)
    }

    @Test
    fun `loadPhotoDetail이 null 오류 메시지를 처리한다`() = runTest {
        // Given
        val photoId = "test-photo-id"

        coEvery { getPhotoDetailUseCase(photoId) } returns Result.failure(Exception())

        // When
        viewModel.loadPhotoDetail(photoId)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `loadPhotoDetail은 여러 북마크에서 정확한 북마크 상태를 확인한다`() = runTest {
        // Given
        val photoId = "test-photo-id"
        val bookmarks = listOf(
            Bookmark(id = "other-id-1", description = "Other", imageUrl = "url1"),
            mockBookmark,
            Bookmark(id = "other-id-2", description = "Other", imageUrl = "url2")
        )

        coEvery { getPhotoDetailUseCase(photoId) } returns Result.success(mockPhotoDetail)
        coEvery { getBookmarksUseCase() } returns flowOf(bookmarks)

        // When
        viewModel.loadPhotoDetail(photoId)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(state.photo, mockPhotoDetail)
        assertFalse(state.isLoading)
        assertTrue(state.isBookmarked)
    }

    @Test
    fun `loadPhotoDetail은 다양한 사진 ID로 동작할 수 있다`() = runTest {
        // Given
        val photoId1 = "photo-id-1"
        val photoId2 = "photo-id-2"
        val photo1 = mockPhotoDetail.copy(id = photoId1)
        val photo2 = mockPhotoDetail.copy(id = photoId2)

        coEvery { getPhotoDetailUseCase(photoId1) } returns Result.success(photo1)
        coEvery { getPhotoDetailUseCase(photoId2) } returns Result.success(photo2)
        coEvery { getBookmarksUseCase() } returns flowOf(emptyList())

        // When
        viewModel.loadPhotoDetail(photoId1)
        advanceUntilIdle()
        val state1 = viewModel.uiState.value

        viewModel.loadPhotoDetail(photoId2)
        advanceUntilIdle()
        val state2 = viewModel.uiState.value

        // Then
        assertEquals(state1.photo?.id, photoId1)
        assertEquals(state2.photo?.id, photoId2)
    }


    // addBookmark 테스트

    @Test
    fun `addBookmark는 UseCase를 호출하고 북마크 상태를 업데이트한다`() = runTest {
        // Given
        coEvery { addBookmarkUseCase(any()) } returns Unit

        // When
        viewModel.addBookmark(mockPhotoDetail)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.isBookmarked)
        coVerify { addBookmarkUseCase(any()) }
    }

    @Test
    fun `addBookmark는 PhotoDetail을 Bookmark로 올바르게 변환한다`() = runTest {
        // Given
        coEvery { addBookmarkUseCase(any()) } returns Unit

        // When
        viewModel.addBookmark(mockPhotoDetail)
        advanceUntilIdle()

        // Then
        coVerify {
            addBookmarkUseCase.invoke(match { bookmark ->
                bookmark.id == mockPhotoDetail.id &&
                        bookmark.description == mockPhotoDetail.description &&
                        bookmark.imageUrl == mockPhotoDetail.urls.regular
            })
        }
    }

    @Test
    fun `addBookmark가 설명이 없는 사진을 처리한다`() = runTest {
        // Given
        val photoWithNullDesc = mockPhotoDetail.copy(description = null)

        coEvery { addBookmarkUseCase(any()) } returns Unit

        // When
        viewModel.addBookmark(photoWithNullDesc)
        advanceUntilIdle()

        // Then
        coVerify {
            addBookmarkUseCase.invoke(match { bookmark -> bookmark.description == "" })
        }
    }

    @Test
    fun `addBookmark는 상태를 즉시 업데이트한다`() = runTest {
        // Given
        coEvery { addBookmarkUseCase(any()) } returns Unit
        assertFalse(viewModel.uiState.value.isBookmarked)

        // When
        viewModel.addBookmark(mockPhotoDetail)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.isBookmarked)
    }


    // deleteBookmark 테스트

    @Test
    fun `deleteBookmark는 UseCase를 호출하고 북마크 상태를 업데이트한다`() = runTest {
        // Given
        coEvery { addBookmarkUseCase(any()) } returns Unit
        coEvery { deleteBookmarkUseCase(any()) } returns Unit
        viewModel.addBookmark(mockPhotoDetail) // 초기 북마크 상태 설정
        advanceUntilIdle()

        // When
        viewModel.deleteBookmark(mockPhotoDetail)
        advanceUntilIdle()

        // Then
        coVerify { deleteBookmarkUseCase.invoke(any()) }
        assertFalse(viewModel.uiState.value.isBookmarked)
    }

    @Test
    fun `deleteBookmark가 PhotoDetail을 Bookmark로 올바르게 변환한다`() = runTest {
        // Given
        coEvery { deleteBookmarkUseCase(any()) } returns Unit

        // When
        viewModel.deleteBookmark(mockPhotoDetail)
        advanceUntilIdle()

        // Then
        coVerify {
            deleteBookmarkUseCase.invoke(match { bookmark ->
                bookmark.id == mockPhotoDetail.id &&
                        bookmark.description == mockPhotoDetail.description &&
                        bookmark.imageUrl == mockPhotoDetail.urls.regular
            })
        }
    }

    @Test
    fun `deleteBookmark가 설명이 없는 사진을 처리한다`() = runTest {
        // Given
        val photoWithNullDesc = mockPhotoDetail.copy(description = null)
        coEvery { deleteBookmarkUseCase(any()) } returns Unit

        // When
        viewModel.deleteBookmark(photoWithNullDesc)
        advanceUntilIdle()

        // Then
        coVerify { deleteBookmarkUseCase.invoke(match { bookmark -> bookmark.description == "" }) }
    }

    @Test
    fun `deleteBookmark는 상태를 즉시 업데이트한다`() = runTest {
        // Given
        coEvery { addBookmarkUseCase(any()) } returns Unit
        coEvery { deleteBookmarkUseCase(any()) } returns Unit

        viewModel.addBookmark(mockPhotoDetail)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isBookmarked)

        // When
        viewModel.deleteBookmark(mockPhotoDetail)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isBookmarked)
    }


    // 상태 전이 테스트

    @Test
    fun `북마크 상태는 올바르게 토글된다`() = runTest {
        // Given
        coEvery { addBookmarkUseCase(any()) } returns Unit
        coEvery { deleteBookmarkUseCase(any()) } returns Unit

        // 초기 상태
        assertFalse(viewModel.uiState.value.isBookmarked)

        // 북마크 추가
        viewModel.addBookmark(mockPhotoDetail)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isBookmarked)

        // 북마크 삭제
        viewModel.deleteBookmark(mockPhotoDetail)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isBookmarked)

        // 다시 추가
        viewModel.addBookmark(mockPhotoDetail)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isBookmarked)
    }

    @Test
    fun `올바른 초기 상태를 가진다`() {
        // Then
        val state = viewModel.uiState.value
        assertNull(state.photo)
        assertTrue(state.isLoading)
        assertNull(state.error)
        assertFalse(state.isBookmarked)
    }

    @Test
    fun `loadPhotoDetail은 이전에 생긴 오류를 지운다`() = runTest {
        // Given - 첫 번째 실패
        val photoId1 = "error-id"

        coEvery { getPhotoDetailUseCase(photoId1) } returns Result.failure(Exception("Error 1"))

        viewModel.loadPhotoDetail(photoId1)
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.error)

        // When - 두 번째 성공
        val photoId2 = "success-id"

        coEvery { getPhotoDetailUseCase(photoId2) } returns Result.success(mockPhotoDetail)
        coEvery { getBookmarksUseCase() } returns flowOf(emptyList())
        viewModel.loadPhotoDetail(photoId2)
        advanceUntilIdle()

        // Then - 오류가 해제되어야 함
        assertNull(viewModel.uiState.value.error)
        assertNotNull(viewModel.uiState.value.photo)
    }
}