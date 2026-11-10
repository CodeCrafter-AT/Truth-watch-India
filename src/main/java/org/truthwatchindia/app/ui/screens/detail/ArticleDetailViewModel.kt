package org.truthwatchindia.app.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.truthwatchindia.app.TruthWatchApp
import org.truthwatchindia.app.data.model.WpPost
import org.truthwatchindia.app.data.model.WpRendered
import org.truthwatchindia.app.domain.repository.NewsRepository

data class ArticleDetailUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val post: WpPost? = null,
    val isBookmarked: Boolean = false,
    val isOfflineCopy: Boolean = false
)

class ArticleDetailViewModel(
    private val postId: Long,
    private val repository: NewsRepository = TruthWatchApp.instance.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArticleDetailUiState())
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    init {
        observeBookmarkStatus()
        loadArticle()
    }

    private fun observeBookmarkStatus() {
        viewModelScope.launch {
            repository.isBookmarked(postId).collect { bookmarked ->
                _uiState.update { it.copy(isBookmarked = bookmarked) }
            }
        }
    }

    fun loadArticle() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.getPostById(postId)
            result.onSuccess { post ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        post = post,
                        isOfflineCopy = false
                    )
                }
            }.onFailure { _ ->
                // Check offline fallback in cache
                val cached = repository.getCachedArticleById(postId)
                if (cached != null) {
                    val fallbackPost = WpPost(
                        id = cached.postId,
                        date = cached.date,
                        title = WpRendered(cached.title),
                        content = WpRendered(cached.content),
                        excerpt = WpRendered(cached.excerpt),
                        link = cached.link
                    )
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null,
                            post = fallbackPost,
                            isOfflineCopy = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "नेटवर्क कनेक्शन जांचें और पुनः प्रयास करें।"
                        )
                    }
                }
            }
        }
    }

    fun toggleBookmark() {
        val currentPost = _uiState.value.post ?: return
        viewModelScope.launch {
            repository.toggleBookmark(currentPost)
        }
    }

    class Factory(
        private val postId: Long,
        private val repository: NewsRepository? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repo = repository ?: TruthWatchApp.instance.repository
            return ArticleDetailViewModel(postId, repo) as T
        }
    }
}
