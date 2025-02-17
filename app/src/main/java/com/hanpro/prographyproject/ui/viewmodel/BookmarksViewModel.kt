package com.hanpro.prographyproject.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hanpro.prographyproject.data.source.local.Bookmark
import com.hanpro.prographyproject.domain.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val repository: BookmarkRepository,
    application: Application,
) : AndroidViewModel(application) {

    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks

    fun loadBookmarks() {
        viewModelScope.launch {
            _bookmarks.value = repository.getBookmarks()
        }
    }

    fun addBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            repository.addBookmark(bookmark)
            loadBookmarks()
        }
    }

    fun deleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            repository.deleteBookmark(bookmark)
            loadBookmarks()
        }
    }
}