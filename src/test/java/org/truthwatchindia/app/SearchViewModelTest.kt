package org.truthwatchindia.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.truthwatchindia.app.data.api.Cf7Response
import org.truthwatchindia.app.data.api.WordPressApiService
import org.truthwatchindia.app.data.model.WpCategory
import org.truthwatchindia.app.data.model.WpPost
import org.truthwatchindia.app.data.model.WpRendered
import org.truthwatchindia.app.data.repository.NewsRepositoryImpl
import org.truthwatchindia.app.ui.screens.search.SearchViewModel
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeBookmarkDao: FakeBookmarkDao
    private lateinit var fakeCacheDao: FakeArticleCacheDao
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeBookmarkDao = FakeBookmarkDao()
        fakeCacheDao = FakeArticleCacheDao()

        val sampleResults = listOf(
            WpPost(
                id = 8802,
                date = "2026-11-04T12:00:00",
                title = WpRendered("लखनऊ में अखिलेश यादव के खिलाफ लगे बॉयकॉट सपा के बैनर"),
                content = WpRendered("सामग्री"),
                categories = listOf(7, 3),
                link = "https://truthwatchindia.org/8802"
            ),
            WpPost(
                id = 8805,
                date = "2026-11-04T13:00:00",
                title = WpRendered("धीरेंद्र शास्त्री के खिलाफ लखनऊ में सपा"),
                content = WpRendered("सामग्री 2"),
                categories = listOf(3),
                link = "https://truthwatchindia.org/8805"
            )
        )

        val dummyApi = object : WordPressApiService {
            override suspend fun getPosts(perPage: Int, page: Int, categories: Long?, search: String?, embed: String?) =
                Response.success(sampleResults)

            override suspend fun getLatestHeadlines(perPage: Int, embed: String?) = Response.success(emptyList<WpPost>())
            override suspend fun getPost(id: Long, embed: String?) = Response.success(sampleResults[0])
            override suspend fun getCategories(perPage: Int, orderby: String, order: String) =
                Response.success(listOf(WpCategory(id = 3, name = "लखनऊ", slug = "lucknow", count = 617)))
            override suspend fun getPage(id: Long) = Response.success(org.truthwatchindia.app.data.model.WpPage(id = id))
            override suspend fun submitFeedback(formId: Long, name: String, email: String, subject: String, message: String, unitTag: String) =
                Response.success(Cf7Response(status = "mail_sent", message = "OK"))
        }

        val repository = NewsRepositoryImpl(
            api = dummyApi,
            bookmarkDao = fakeBookmarkDao,
            articleCacheDao = fakeCacheDao
        )

        viewModel = SearchViewModel(repository)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testExecuteSearchReturnsResultsAndRecordsHistory() = runTest {
        viewModel.executeSearch("लखनऊ")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.hasSearched)
        assertEquals(2, state.results.size)
        assertEquals(8802L, state.results[0].id)
        assertTrue(state.recentQueries.contains("लखनऊ"))
    }

    @Test
    fun testCategoryFilterAppliesToResults() = runTest {
        val cat7 = WpCategory(id = 7, name = "उत्तर प्रदेश", slug = "up", count = 405)
        viewModel.selectCategory(cat7)
        viewModel.executeSearch("लखनऊ")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.results.size)
        assertEquals(8802L, state.results[0].id)
    }

    @Test
    fun testClearRecentQueries() = runTest {
        viewModel.executeSearch("अखिलेश")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.recentQueries.isNotEmpty())

        viewModel.clearRecentQueries()
        assertTrue(viewModel.uiState.value.recentQueries.isEmpty())
    }
}
