package org.truthwatchindia.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.truthwatchindia.app.TruthWatchApp
import org.truthwatchindia.app.data.model.WpCategory
import org.truthwatchindia.app.data.model.WpPost
import org.truthwatchindia.app.data.model.WpRendered
import org.truthwatchindia.app.domain.repository.NewsRepository

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val isOffline: Boolean = false,
    val categories: List<WpCategory> = emptyList(),
    val featuredPost: WpPost? = null,
    val latestPosts: List<WpPost> = emptyList(),
    val upPosts: List<WpPost> = emptyList(),
    val lucknowPosts: List<WpPost> = emptyList(),
    val nationalPosts: List<WpPost> = emptyList(),
    val worldPosts: List<WpPost> = emptyList(),
    val breakingHeadlines: List<String> = emptyList()
)

class HomeViewModel(
    private val repository: NewsRepository = TruthWatchApp.instance.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData(isRefresh = false)
    }

    fun refresh() {
        loadData(isRefresh = true)
    }

    private fun loadData(isRefresh: Boolean) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            try {
                // Fetch categories, breaking, latest, and specific editorial rails concurrently
                val categoriesDeferred = async { repository.getCategories(perPage = 50) }
                val latestDeferred = async { repository.getPosts(page = 1, perPage = 15) }
                val upDeferred = async { repository.getPosts(page = 1, perPage = 4, categoryId = 7) } // उत्तर प्रदेश
                val lucknowDeferred = async { repository.getPosts(page = 1, perPage = 4, categoryId = 3) } // लखनऊ
                val nationalDeferred = async { repository.getPosts(page = 1, perPage = 4, categoryId = 5) } // देश
                val worldDeferred = async { repository.getPosts(page = 1, perPage = 4, categoryId = 6) } // विदेश

                val catResult = categoriesDeferred.await()
                val latestResult = latestDeferred.await()
                val upResult = upDeferred.await()
                val lucknowResult = lucknowDeferred.await()
                val nationalResult = nationalDeferred.await()
                val worldResult = worldDeferred.await()

                if (latestResult.isSuccess) {
                    val posts = latestResult.getOrNull().orEmpty()
                    val categories = catResult.getOrNull().orEmpty()
                    val upPosts = upResult.getOrNull().orEmpty()
                    val lucknowPosts = lucknowResult.getOrNull().orEmpty()
                    val nationalPosts = nationalResult.getOrNull().orEmpty()
                    val worldPosts = worldResult.getOrNull().orEmpty()

                    val breakingTitles = posts.take(5).map { it.getCleanTitle() }
                    val featured = posts.firstOrNull()
                    val remainingLatest = if (posts.size > 1) posts.drop(1) else emptyList()

                    // Asynchronously cache feeds for offline resiliency
                    repository.cacheArticles("latest", posts)
                    if (upPosts.isNotEmpty()) repository.cacheArticles("up", upPosts)
                    if (lucknowPosts.isNotEmpty()) repository.cacheArticles("lucknow", lucknowPosts)
                    if (nationalPosts.isNotEmpty()) repository.cacheArticles("national", nationalPosts)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = null,
                            isOffline = false,
                            categories = categories,
                            featuredPost = featured,
                            latestPosts = remainingLatest,
                            upPosts = upPosts,
                            lucknowPosts = lucknowPosts,
                            nationalPosts = nationalPosts,
                            worldPosts = worldPosts,
                            breakingHeadlines = breakingTitles
                        )
                    }
                } else {
                    loadFromCache()
                }
            } catch (e: Exception) {
                loadFromCache()
            }
        }
    }

    private suspend fun loadFromCache() {
        val cachedLatest = repository.getCachedArticles("latest").firstOrNull().orEmpty()
        val cachedUp = repository.getCachedArticles("up").firstOrNull().orEmpty()
        val cachedLucknow = repository.getCachedArticles("lucknow").firstOrNull().orEmpty()

        if (cachedLatest.isNotEmpty()) {
            val posts = cachedLatest.map { cached ->
                WpPost(
                    id = cached.postId,
                    date = cached.date,
                    title = WpRendered(cached.title),
                    content = WpRendered(cached.content),
                    excerpt = WpRendered(cached.excerpt),
                    link = cached.link
                )
            }
            val featured = posts.firstOrNull()
            val remaining = if (posts.size > 1) posts.drop(1) else emptyList()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    isOffline = true,
                    featuredPost = featured,
                    latestPosts = remaining,
                    breakingHeadlines = posts.take(3).map { p -> p.getCleanTitle() },
                    errorMessage = null
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    isOffline = true,
                    errorMessage = "समाचार लोड करने में असमर्थ। कृपया इंटरनेट कनेक्शन जांचें।"
                )
            }
        }
    }

    class Factory(
        private val repository: NewsRepository? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repo = repository ?: TruthWatchApp.instance.repository
            return HomeViewModel(repo) as T
        }
    }
}
