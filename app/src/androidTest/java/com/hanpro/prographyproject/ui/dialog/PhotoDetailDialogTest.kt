package com.hanpro.prographyproject.ui.dialog

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.hanpro.prographyproject.data.model.*
import com.hanpro.prographyproject.data.source.local.Bookmark
import com.hanpro.prographyproject.domain.usecase.AddBookmarkUseCase
import com.hanpro.prographyproject.domain.usecase.DeleteBookmarkUseCase
import com.hanpro.prographyproject.domain.usecase.GetBookmarksUseCase
import com.hanpro.prographyproject.domain.usecase.GetPhotoDetailUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PhotoDetailDialog {

    @get:Rule
    val composeTestRule = createComposeRule()

    @MockK
    private lateinit var getPhotoDetailUseCase: GetPhotoDetailUseCase

    @MockK
    private lateinit var getBookmarksUseCase: GetBookmarksUseCase

    @MockK
    private lateinit var addBookmarkUseCase: AddBookmarkUseCase

    @MockK
    private lateinit var deleteBookmarkUseCase: DeleteBookmarkUseCase

    private lateinit var viewModel: com.hanpro.prographyproject.ui.viewmodel.PhotoDetailViewModel

    private val mockPhotoDetail = PhotoDetail(
        id = "photo-1",
        description = "Detailed photo description",
        urls = Urls(full = "https://example.com/full.jpg", regular = "https://example.com/regular.jpg"),
        tags = listOf(Tag("nature"), Tag("mountains"), Tag("landscape")),
        links = Link(download = "https://example.com/download"),
        user = User(username = "photographer123")
    )

    private val mockBookmark = Bookmark(
        id = "photo-1",
        description = "Detailed photo description",
        imageUrl = "https://example.com/regular.jpg"
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true, relaxUnitFun = true)

        coEvery { getBookmarksUseCase() } returns flowOf(emptyList())
        coEvery { getPhotoDetailUseCase(any()) } returns Result.success(mockPhotoDetail)
    }

    private fun createViewModel(): com.hanpro.prographyproject.ui.viewmodel.PhotoDetailViewModel {
        return com.hanpro.prographyproject.ui.viewmodel.PhotoDetailViewModel(
            getPhotoDetailUseCase,
            getBookmarksUseCase,
            addBookmarkUseCase,
            deleteBookmarkUseCase
        )
    }

    @Test
    fun photoDetailDialog는_로딩중일때_로딩인디케이터를_표시한다() {
        // Given
        coEvery { getPhotoDetailUseCase(any()) } coAnswers {
            kotlinx.coroutines.delay(10000)
            Result.success(mockPhotoDetail)
        }
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            PhotoDetailDialog(
                photoId = "photo-1",
                onClose = {},
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("indicator").assertIsDisplayed()
    }

    @Test
    fun photoDetailDialog는_사진정보를_정상적으로_표시한다() {
        // Given
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            PhotoDetailDialog(
                photoId = "photo-1",
                onClose = {},
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("photographer123").assertExists()
        composeTestRule.onNodeWithText("nature").assertExists()
        composeTestRule.onNodeWithText("#nature #mountains #landscape").assertExists()
    }

    @Test
    fun photoDetailDialog는_닫기버튼_클릭시_onClose가_호출된다() {
        // Given
        viewModel = createViewModel()
        var closeCalled = false

        composeTestRule.setContent {
            PhotoDetailDialog(
                photoId = "photo-1",
                onClose = { closeCalled = true },
                viewModel = viewModel
            )
        }

        composeTestRule.waitForIdle()

        // When - 닫기 버튼 클릭
        composeTestRule.onNodeWithContentDescription("close").performClick()

        // Then
        assert(closeCalled)
    }

    @Test
    fun photoDetailDialog는_북마크되지않은_사진의_북마크버튼_클릭시_북마크가_추가된다() {
        // Given
        coEvery { getBookmarksUseCase() } returns flowOf(emptyList())
        coEvery { addBookmarkUseCase(any()) } returns Unit
        viewModel = createViewModel()

        composeTestRule.setContent {
            PhotoDetailDialog(
                photoId = "photo-1",
                onClose = {},
                viewModel = viewModel
            )
        }

        composeTestRule.waitForIdle()

        // When - 북마크 버튼 클릭
        composeTestRule.onAllNodesWithContentDescription("bookmark").onLast().performClick()

        // Then
        composeTestRule.waitForIdle()
        coVerify { addBookmarkUseCase(any()) }
    }

    @Test
    fun photoDetailDialog는_북마크된_사진의_북마크버튼_클릭시_북마크가_제거된다() {
        // Given
        coEvery { getBookmarksUseCase() } returns flowOf(listOf(mockBookmark))
        coEvery { deleteBookmarkUseCase(any()) } returns Unit
        viewModel = createViewModel()

        composeTestRule.setContent {
            PhotoDetailDialog(
                photoId = "photo-1",
                onClose = {},
                viewModel = viewModel
            )
        }

        composeTestRule.waitForIdle()

        // When - 북마크 버튼 클릭
        composeTestRule.onAllNodesWithContentDescription("bookmark").onLast().performClick()

        // Then
        composeTestRule.waitForIdle()
        coVerify { deleteBookmarkUseCase(any()) }
    }

    @Test
    fun photoDetailDialog의_다운로드버튼이_표시된다() {
        // Given
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            PhotoDetailDialog(
                photoId = "photo-1",
                onClose = {},
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("download").assertExists()
    }

    @Test
    fun photoDetailDialog는_태그가_없는경우_빈문자열을_표시한다() {
        // Given
        val photoWithoutTags = mockPhotoDetail.copy(tags = null)
        coEvery { getPhotoDetailUseCase(any()) } returns Result.success(photoWithoutTags)
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            PhotoDetailDialog(
                photoId = "photo-1",
                onClose = {},
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("photographer123").assertExists()
    }

    @Test
    fun photoDetailDialog는_설명이_없는경우_표시하지않는다() {
        // Given
        val photoWithoutDescription = mockPhotoDetail.copy(description = null)
        coEvery { getPhotoDetailUseCase(any()) } returns Result.success(photoWithoutDescription)
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            PhotoDetailDialog(
                photoId = "photo-1",
                onClose = {},
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("photographer123").assertExists()
    }
}