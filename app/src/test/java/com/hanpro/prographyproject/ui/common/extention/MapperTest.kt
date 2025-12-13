package com.hanpro.prographyproject.ui.common.extention

import com.hanpro.prographyproject.data.model.*
import com.hanpro.prographyproject.ui.common.extension.toBookmark
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MapperTest {

    private val mockPhotoDetail = PhotoDetail(
        id = "test-photo-id",
        description = "Test photo description",
        urls = Urls(
            full = "https://example.com/full.jpg",
            regular = "https://example.com/regular.jpg"
        ),
        tags = listOf(Tag(title = "nature"), Tag(title = "landscape")),
        links = Link(download = "https://example.com/download"),
        user = User(username = "testuser")
    )

    @Test
    fun `toBookmark는 PhotoDetail을 Bookmark로 올바르게 매핑한다`() {
        // When
        val bookmark = mockPhotoDetail.toBookmark()

        // Then
        assertEquals(mockPhotoDetail.id, bookmark.id)
        assertEquals(mockPhotoDetail.description, bookmark.description)
        assertEquals(mockPhotoDetail.urls.regular, bookmark.imageUrl)
    }

    @Test
    fun `toBookmark는 imageUrl에 regular URL을 사용한다`() {
        // Given
        val photoDetail = mockPhotoDetail.copy(
            urls = Urls(
                full = "https://example.com/full-image.jpg",
                regular = "https://example.com/regular-image.jpg"
            )
        )

        // When
        val bookmark = photoDetail.toBookmark()

        // Then
        assertEquals("https://example.com/regular-image.jpg", bookmark.imageUrl)
        assertNotEquals(photoDetail.urls.full, bookmark.imageUrl)
    }

    @Test
    fun `toBookmark는 null description을 빈 문자열로 변환하여 처리한다`() {
        // Given
        val photoDetailWithNullDesc = mockPhotoDetail.copy(description = null)

        // When
        val bookmark = photoDetailWithNullDesc.toBookmark()

        // Then
        assertEquals("", bookmark.description)
        assertNotNull(bookmark.description)
    }

    @Test
    fun `toBookmark는 빈 description을 공백으로 처리한다`() {
        // Given
        val photoDetailWithEmptyDesc = mockPhotoDetail.copy(description = "")

        // When
        val bookmark = photoDetailWithEmptyDesc.toBookmark()

        // Then
        assertEquals("", bookmark.description)
    }

    @Test
    fun `toBookmark는 긴 description을 처리한다`() {
        // Given
        val longDescription = "A".repeat(1000)
        val photoDetailWithLongDesc = mockPhotoDetail.copy(description = longDescription)

        // When
        val bookmark = photoDetailWithLongDesc.toBookmark()

        // Then
        assertEquals(longDescription, bookmark.description)
        assertEquals(1000, bookmark.description.length)
    }

    @Test
    fun `toBookmark는 description에서 특수 문자를 그대로 사용한다`() {
        // Given
        val specialDesc = "Test with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?"
        val photoDetail = mockPhotoDetail.copy(description = specialDesc)

        // When
        val bookmark = photoDetail.toBookmark()

        // Then
        assertEquals(specialDesc, bookmark.description)
    }

    @Test
    fun `toBookmark는 description에서 유니코드 문자를 그대로 사용한다`() {
        // Given
        val unicodeDesc = "테스트 🌟 Test 日本語 Тест"
        val photoDetail = mockPhotoDetail.copy(description = unicodeDesc)

        // When
        val bookmark = photoDetail.toBookmark()

        // Then
        assertEquals(unicodeDesc, bookmark.description)
    }

    @Test
    fun `toBookmark는 다양한 ID 형식을 처리한다`() {
        // Given
        val idFormats = listOf(
            "simple-id",
            "id_with_underscore",
            "id-with-dash",
            "ID123",
            "very-long-id-" + "x".repeat(50)
        )

        idFormats.forEach { id ->
            // Given
            val photoDetail = mockPhotoDetail.copy(id = id)

            // When
            val bookmark = photoDetail.toBookmark()

            // Then
            assertEquals(id, bookmark.id)
        }
    }

    @Test
    fun `toBookmark는 Tag 필드를 무시한다`() {
        // Given
        val photoWithTags = mockPhotoDetail.copy(
            tags = listOf(Tag("tag1"), Tag("tag2"), Tag("tag3"))
        )

        // When
        val bookmark = photoWithTags.toBookmark()

        // Then
        assertEquals(photoWithTags.id, bookmark.id)
        assertEquals(photoWithTags.description, bookmark.description)
        assertEquals(photoWithTags.urls.regular, bookmark.imageUrl)
    }

    @Test
    fun `toBookmark가 Link 필드를 무시한다`() {
        // Given
        val photoWithDifferentLink = mockPhotoDetail.copy(
            links = Link(download = "https://different-download-url.com")
        )

        // When
        val bookmark = photoWithDifferentLink.toBookmark()

        // Then
        assertEquals(photoWithDifferentLink.id, bookmark.id)
        assertEquals(photoWithDifferentLink.description, bookmark.description)
        assertEquals(photoWithDifferentLink.urls.regular, bookmark.imageUrl)
    }

    @Test
    fun `toBookmark는 User 필드를 무시한다`() {
        // Given
        val photoWithUser = mockPhotoDetail.copy(
            user = User(username = "differentuser")
        )

        // When
        val bookmark = photoWithUser.toBookmark()

        // Then
        assertEquals(photoWithUser.id, bookmark.id)
        assertEquals(photoWithUser.description, bookmark.description)
        assertEquals(photoWithUser.urls.regular, bookmark.imageUrl)
    }

    @Test
    fun `toBookmark는 독립적인 북마크 객체를 생성한다`() {
        // Given
        val photoDetail = mockPhotoDetail.copy()

        // When
        val bookmark = photoDetail.toBookmark()

        // Then
        assertEquals(mockPhotoDetail.id, bookmark.id)
        assertEquals(mockPhotoDetail.description, bookmark.description)
        assertEquals(mockPhotoDetail.urls.regular, bookmark.imageUrl)
    }

    @Test
    fun `toBookmark는 쿼리 매개변수가 있는 URL을 처리한다`() {
        // Given
        val urlWithParams = "https://example.com/photo.jpg?ixid=123&ixlib=rb-4.0.3&q=80&w=1080"
        val photoDetail = mockPhotoDetail.copy(
            urls = Urls(
                full = "https://example.com/full.jpg?ixid=123",
                regular = urlWithParams
            )
        )

        // When
        val bookmark = photoDetail.toBookmark()

        // Then
        assertEquals(urlWithParams, bookmark.imageUrl)
    }

    @Test
    fun `toBookmark는 같은 결과를 여러 번 호출할 수 있다`() {
        // When
        val bookmark1 = mockPhotoDetail.toBookmark()
        val bookmark2 = mockPhotoDetail.toBookmark()

        // Then
        assertEquals(bookmark1.id, bookmark2.id)
        assertEquals(bookmark1.description, bookmark2.description)
        assertEquals(bookmark1.imageUrl, bookmark2.imageUrl)
    }

    @Test
    fun `toBookmark는 설명에서 공백을 처리한다`() {
        // Given
        val descriptionsWithWhitespace = listOf(
            "  Leading whitespace",
            "Trailing whitespace  ",
            "  Both sides  ",
            "Multiple   spaces   between",
            "Line\nbreak",
            "Tab\there"
        )

        descriptionsWithWhitespace.forEach { desc ->
            // Given
            val photoDetail = mockPhotoDetail.copy(description = desc)

            // When
            val bookmark = photoDetail.toBookmark()

            // Then - Whitespace is preserved
            assertEquals(desc, bookmark.description)
        }
    }

    @Test
    fun `toBookmark는 빈 문자열 ID를 처리한다`() {
        // Given
        val photoDetail = mockPhotoDetail.copy(id = "")

        // When
        val bookmark = photoDetail.toBookmark()

        // Then
        assertEquals("", bookmark.id)
    }

    @Test
    fun `toBookmark는 최소로 유효한 필드를 가진 PhotoDetail을 처리한다`() {
        // Given - PhotoDetail with minimum required fields
        val minimalPhoto = PhotoDetail(
            id = "minimal",
            description = null,
            urls = Urls(full = "https://full.jpg", regular = "https://regular.jpg"),
            tags = null,
            links = Link(download = "https://download.jpg"),
            user = User(username = "user")
        )

        // When
        val bookmark = minimalPhoto.toBookmark()

        // Then
        assertEquals("minimal", bookmark.id)
        assertEquals("", bookmark.description)
        assertEquals("https://regular.jpg", bookmark.imageUrl)
    }
}