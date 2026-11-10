package org.truthwatchindia.app.ui.screens.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.truthwatchindia.app.TruthWatchApp
import org.truthwatchindia.app.data.local.entity.BookmarkEntity
import org.truthwatchindia.app.domain.repository.NewsRepository

data class BookmarksUiState(
    val bookmarks: List<BookmarkEntity> = emptyList(),
    val isLoading: Boolean = false
)

class BookmarksViewModel(
    private val repository: NewsRepository = TruthWatchApp.instance.repository
) : ViewModel() {

    val uiState: StateFlow<BookmarksUiState> = repository.getAllBookmarks()
        .map { list -> BookmarksUiState(bookmarks = list, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BookmarksUiState(isLoading = true)
        )

    fun removeBookmark(postId: Long) {
        viewModelScope.launch {
            repository.removeBookmark(postId)
        }
    }

    class Factory(
        private val repository: NewsRepository? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repo = repository ?: TruthWatchApp.instance.repository
            return BookmarksViewModel(repo) as T
        }
    }
}
