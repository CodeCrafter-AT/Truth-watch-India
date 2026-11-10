package org.truthwatchindia.app.ui.screens.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.truthwatchindia.app.data.api.RetrofitClient
import org.truthwatchindia.app.data.model.WpPost

data class CategoryUiState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val posts: List<WpPost> = emptyList(),
    val currentPage: Int = 1,
    val hasMore: Boolean = true
)

class CategoryViewModel(
    private val categoryId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        loadPosts(page = 1)
    }

    fun loadMore() {
        val currentState = _uiState.value
        if (!currentState.isLoadingMore && currentState.hasMore && !currentState.isLoading) {
            loadPosts(page = currentState.currentPage + 1)
        }
    }

    fun refresh() {
        loadPosts(page = 1)
    }

    private fun loadPosts(page: Int) {
        viewModelScope.launch {
            if (page == 1) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoadingMore = true) }
            }

            try {
                val response = RetrofitClient.api.getPosts(
                    perPage = 15,
                    page = page,
                    categories = categoryId
                )

                if (response.isSuccessful) {
                    val newPosts = response.body().orEmpty()
                    _uiState.update { current ->
                        val combined = if (page == 1) newPosts else current.posts + newPosts
                        current.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            errorMessage = null,
                            posts = combined,
                            currentPage = page,
                            hasMore = newPosts.size >= 15
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            errorMessage = if (page == 1) "समाचार लोड करने में असमर्थ" else null,
                            hasMore = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        errorMessage = if (page == 1) "नेटवर्क त्रुटि। कृपया पुनः प्रयास करें।" else null
                    )
                }
            }
        }
    }

    class Factory(private val categoryId: Long) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CategoryViewModel(categoryId) as T
        }
    }
}
