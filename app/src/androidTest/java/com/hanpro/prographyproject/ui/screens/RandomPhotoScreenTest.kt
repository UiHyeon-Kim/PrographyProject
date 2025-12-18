package com.hanpro.prographyproject.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
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
}
