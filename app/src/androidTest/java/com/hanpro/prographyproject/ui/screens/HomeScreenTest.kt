package com.hanpro.prographyproject.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.hanpro.prographyproject.common.utils.NetworkEvent
import com.hanpro.prographyproject.common.utils.NetworkManager
import com.hanpro.prographyproject.data.model.*
import com.hanpro.prographyproject.data.source.local.Bookmark
import com.hanpro.prographyproject.domain.usecase.*
import com.hanpro.prographyproject.ui.viewmodel.PhotoViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    // 이후 호출되는 함수를 테스트 생명주기에 통합
    // 각 테스트 메서드 실행 전 후 자동으로 setup/teardown 수행
    @get:Rule
    val composeTestRule = createComposeRule()

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

    private val networkStateFlow = MutableStateFlow(true)
    private val networkEventFlow = MutableStateFlow<NetworkEvent?>(null)

    private val mockBookmark = Bookmark(
        id = "bookmark-1",
        description = "Bookmarked photo",
        imageUrl = "https://example.com/bookmark.jpg"
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true, relaxUnitFun = true)

        coEvery { networkManager.networkState } returns networkStateFlow
        coEvery { networkManager.networkEvent } returns networkEventFlow
        coEvery { networkManager.checkNetworkConnection() } returns true

        coEvery { getLatestPhotosUseCase(any(), any()) } returns Result.success(emptyList())
        coEvery { getRandomPhotosUseCase(any()) } returns Result.success(emptyList())
        coEvery { getBookmarksUseCase() } returns flowOf(emptyList())
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

    // 컴포넌트 렌더링 테스트

    @Test
    fun HomeScreen은_로딩중일때_스켈레톤UI를_표시한다() {
        // Given
        // 스켈레톤이 너무 빠르게 지나가 3초 지연
        coEvery { getLatestPhotosUseCase(any(), any()) } coAnswers {
            delay(3000)
            Result.success(emptyList())
        }

        coEvery { getRandomPhotosUseCase(any()) } coAnswers {
            delay(3000)
            Result.success(emptyList())
        }

        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel)
        }

        // Then
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("home_skeleton").assertIsDisplayed()
    }

    @Test
    fun HomeScreen은_네트워크연결해제시_NoNetworkScreen을_표시한다() {
        // Given
        networkStateFlow.value = false
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel)
        }

        // Then
        composeTestRule.onNodeWithContentDescription("네트워크 연결 끊김").assertExists()
        composeTestRule.onNodeWithText("인터넷이 원활하지 않아요").assertExists()
        composeTestRule.onNodeWithText("인터넷 연결을 다시 확인해 주세요").assertExists()
        composeTestRule.onNodeWithText("새로고침").assertExists()
    }

    @Test
    fun HomeScreen은_북마크가_있을때_북마크섹션을_표시한다() {
        // Given
        val bookmarks = listOf(mockBookmark, mockBookmark.copy(id = "bookmark-2"))
        coEvery { getBookmarksUseCase() } returns flowOf(bookmarks)
        coEvery { getLatestPhotosUseCase(any(), any()) } returns Result.success(emptyList())
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel)
        }

        // Then
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("북마크").assertExists()
        composeTestRule.onNodeWithText("최신 이미지").assertExists()
    }

    @Test
    fun HomeScreen은_북마크가_없을때_북마크섹션을_표시하지않는다() {
        // Given
        coEvery { getBookmarksUseCase() } returns flowOf(emptyList())
        coEvery { getLatestPhotosUseCase(any(), any()) } returns Result.success(emptyList())
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel)
        }

        // Then
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("북마크").assertDoesNotExist()
        composeTestRule.onNodeWithText("최신 이미지").assertExists()
    }

    @Test
    fun NoNetworkScreen에서_새로고침버튼_클릭시_재연결을_시도한다() {
        // Given
        networkStateFlow.value = false
        viewModel = createViewModel()

        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel)
        }

        // When
        composeTestRule.onNodeWithText("새로고침").performClick()

        // Then
        coVerify { networkManager.checkNetworkConnection() }
    }

    @Test
    fun HomeScreen은_네트워크_연결시_자동으로_재연결된다() {
        // Given
        networkStateFlow.value = false
        viewModel = createViewModel()

        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel)
        }

        // When
        networkEventFlow.value = NetworkEvent.Connected

        // Then
        composeTestRule.waitForIdle()
        coVerify { networkManager.checkNetworkConnection() }
    }
}
