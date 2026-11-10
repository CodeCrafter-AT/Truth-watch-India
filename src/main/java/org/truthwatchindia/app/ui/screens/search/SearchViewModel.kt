package org.truthwatchindia.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.truthwatchindia.app.TruthWatchApp
import org.truthwatchindia.app.data.model.WpCategory
import org.truthwatchindia.app.data.model.WpPost
import org.truthwatchindia.app.domain.repository.NewsRepository

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<WpPost> = emptyList(),
    val errorMessage: String? = null,
    val recentQueries: List<String> = emptyList(),
    val categories: List<WpCategory> = emptyList(),
    val selectedCategory: WpCategory? = null,
    val hasSearched: Boolean = false
)

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val repository: NewsRepository = TruthWatchApp.instance.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        loadCategories()
        observeQuery()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val result = repository.getCategories(perPage = 30)
            result.onSuccess { cats ->
                _uiState.update { it.copy(categories = cats) }
            }
        }
    }

    private fun observeQuery() {
        viewModelScope.launch {
            queryFlow
                .debounce(400)
                .distinctUntilChanged()
                .filter { it.trim().length >= 2 }
                .collect { q ->
                    executeSearch(q)
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        queryFlow.value = newQuery
        if (newQuery.isBlank()) {
            _uiState.update { it.copy(results = emptyList(), hasSearched = false, errorMessage = null) }
        }
    }

    fun selectCategory(category: WpCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
        val currentQuery = _uiState.value.query
        if (currentQuery.isNotBlank()) {
            executeSearch(currentQuery)
        }
    }

    fun onRecentQueryClick(query: String) {
        _uiState.update { it.copy(query = query) }
        executeSearch(query)
    }

    fun clearRecentQueries() {
        _uiState.update { it.copy(recentQueries = emptyList()) }
    }

    fun executeSearch(q: String) {
        val trimmed = q.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    hasSearched = true
                )
            }

            val result = repository.searchPosts(
                query = trimmed,
                page = 1,
                perPage = 25
            )

            result.onSuccess { posts ->
                val filtered = if (_uiState.value.selectedCategory != null) {
                    val catId = _uiState.value.selectedCategory!!.id
                    posts.filter { it.categories.contains(catId) }
                } else {
                    posts
                }

                // Add to recent queries
                val updatedRecent = (listOf(trimmed) + _uiState.value.recentQueries)
                    .distinct()
                    .take(8)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        results = filtered,
                        recentQueries = updatedRecent,
                        errorMessage = null
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "खोज में त्रुटि हुई। कृपया इंटरनेट जांचें।"
                    )
                }
            }
        }
    }

    class Factory(
        private val repository: NewsRepository? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repo = repository ?: TruthWatchApp.instance.repository
            return SearchViewModel(repo) as T
        }
    }
}
