package com.hanpro.prographyproject.ui.screens

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.hanpro.prographyproject.common.utils.NetworkEvent
import com.hanpro.prographyproject.common.utils.NetworkManager
import com.hanpro.prographyproject.data.model.*
import com.hanpro.prographyproject.domain.usecase.*
import com.hanpro.prographyproject.ui.viewmodel.PhotoViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RandomPhotoScreenTest {

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

    private val mockPhotoDetail = PhotoDetail(
        id = "photo-1",
        description = "Random photo",
        urls = Urls(full = "https://example.com/full.jpg", regular = "https://example.com/regular.jpg"),
        tags = listOf(Tag("random")),
        links = Link(download = "https://example.com/download"),
        user = User(username = "randomuser")
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true, relaxUnitFun = true)

        every { networkManager.networkState } returns networkStateFlow
        every { networkManager.networkEvent } returns networkEventFlow
        every { networkManager.checkNetworkConnection() } returns true

        coEvery { getBookmarksUseCase() } returns flowOf(emptyList())
        coEvery { getLatestPhotosUseCase(any(), any()) } returns Result.success(emptyList())
        coEvery { getRandomPhotosUseCase(any()) } returns Result.success(emptyList())
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

    @Test
    fun randomPhotoScreen은_로딩중일때_스켈레톤UI를_표시한다() {
        // Given
        coEvery { getLatestPhotosUseCase(any(), any()) } coAnswers {
            delay(1000)
            Result.success(emptyList())
        }

        coEvery { getRandomPhotosUseCase(any()) } coAnswers {
            delay(1000)
            Result.success(emptyList())
        }

        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            RandomPhotoScreen(viewModel = viewModel)
        }

        // Then
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("random_skeleton").assertIsDisplayed()
    }

    @Test
    fun randomPhotoScreen은_네트워크연결해제시_NoNetworkScreen을_표시한다() {
        // Given
        networkStateFlow.value = false
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            RandomPhotoScreen(viewModel = viewModel)
        }

        // Then
        composeTestRule.onNodeWithContentDescription("네트워크 연결 끊김").assertExists()
        composeTestRule.onNodeWithText("새로고침").assertExists()
    }

    @Test
    fun randomPhotoScreen은_사진목록이_있을때_페이저를_표시한다() {
        // Given
        val photos = listOf(
            mockPhotoDetail,
            mockPhotoDetail.copy(id = "photo-2"),
            mockPhotoDetail.copy(id = "photo-3")
        )
        coEvery { getRandomPhotosUseCase(any()) } returns Result.success(photos)
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            RandomPhotoScreen(viewModel = viewModel)
        }

        // Then
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("pager").assertIsDisplayed()
    }

    @Test
    fun randomPhotoScreen을_왼쪽으로_스와이프시_다음페이지로_이동한다() {
        // Given
        val photos = listOf(
            mockPhotoDetail,
            mockPhotoDetail.copy(id = "photo-2")
        )
        coEvery { getRandomPhotosUseCase(any()) } returns Result.success(photos)

        viewModel = createViewModel()

        lateinit var pagerState: PagerState

        composeTestRule.setContent {
            pagerState = rememberPagerState(pageCount = { photos.size })

            RandomPhotoScreen(viewModel = viewModel, pagerState = pagerState)
        }

        composeTestRule.mainClock.autoAdvance = false

        // When
        composeTestRule.onNodeWithTag("pager").performTouchInput { swipeLeft() }
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Then
        assert(1 == pagerState.currentPage)
    }
}