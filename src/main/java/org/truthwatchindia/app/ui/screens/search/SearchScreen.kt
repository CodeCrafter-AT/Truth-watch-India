package org.truthwatchindia.app.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.truthwatchindia.app.ui.components.CompactArticleRow
import org.truthwatchindia.app.ui.components.ErrorMessage
import org.truthwatchindia.app.ui.components.LoadingIndicator
import org.truthwatchindia.app.ui.theme.CanvasWhite
import org.truthwatchindia.app.ui.theme.CrimsonMaroon
import org.truthwatchindia.app.ui.theme.DarkCharcoal
import org.truthwatchindia.app.ui.theme.DividerStroke
import org.truthwatchindia.app.ui.theme.MetadataGray
import org.truthwatchindia.app.ui.theme.SurfaceLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onPostClick: (Long) -> Unit,
    onBack: (() -> Unit)? = null,
    viewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory())
) {
    val state by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = { viewModel.onQueryChange(it) },
                        placeholder = {
                            Text(
                                text = "समाचार खोजें (Search)...",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 15.sp
                            )
                        },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        trailingIcon = {
                            if (state.query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onQueryChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = Color.White
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.White.copy(alpha = 0.15f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                focusManager.clearFocus()
                                viewModel.executeSearch(state.query)
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CrimsonMaroon,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = SurfaceLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category Filter Chips Row
            if (state.categories.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = state.selectedCategory == null,
                            onClick = { viewModel.selectCategory(null) },
                            label = { Text("सभी श्रेणियां (All)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CrimsonMaroon,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    items(state.categories, key = { it.id }) { cat ->
                        FilterChip(
                            selected = state.selectedCategory?.id == cat.id,
                            onClick = {
                                if (state.selectedCategory?.id == cat.id) {
                                    viewModel.selectCategory(null)
                                } else {
                                    viewModel.selectCategory(cat)
                                }
                            },
                            label = { Text(cat.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CrimsonMaroon,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                HorizontalDivider(color = DividerStroke, thickness = 0.5.dp)
            }

            // Body Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when {
                    state.isLoading -> {
                        LoadingIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    state.errorMessage != null -> {
                        ErrorMessage(
                            message = state.errorMessage ?: "",
                            onRetry = { viewModel.executeSearch(state.query) },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    state.hasSearched && state.results.isEmpty() -> {
                        EmptySearchResultsView(query = state.query)
                    }
                    state.results.isNotEmpty() -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Text(
                                    text = "${state.results.size} परिणाम मिले",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MetadataGray,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                            items(state.results, key = { it.id }) { post ->
                                CompactArticleRow(
                                    post = post,
                                    onClick = { onPostClick(post.id) }
                                )
                            }
                        }
                    }
                    else -> {
                        // Initial State: Recent Searches
                        RecentSearchesView(
                            recentQueries = state.recentQueries,
                            onQueryClick = { viewModel.onRecentQueryClick(it) },
                            onClear = { viewModel.clearRecentQueries() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentSearchesView(
    recentQueries: List<String>,
    onQueryClick: (String) -> Unit,
    onClear: () -> Unit
) {
    if (recentQueries.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MetadataGray.copy(alpha = 0.5f),
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "शीर्षक, विषय या स्थान से खोजें",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MetadataGray)
                )
            }
        }
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "हालिया खोजें (Recent Searches)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal
                    )
                )
                Text(
                    text = "हटाएं",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CrimsonMaroon,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable(onClick = onClear)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            recentQueries.forEach { query ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onQueryClick(query) }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MetadataGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = query,
                        style = MaterialTheme.typography.bodyMedium.copy(color = DarkCharcoal)
                    )
                }
                HorizontalDivider(color = DividerStroke, thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun EmptySearchResultsView(query: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            tint = CrimsonMaroon.copy(alpha = 0.6f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "कोई परिणाम नहीं मिला",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = DarkCharcoal
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "\"$query\" के लिए कोई समाचार प्राप्त नहीं हुआ। कृपया अन्य शब्द या श्रेणी का प्रयास करें।",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MetadataGray,
                textAlign = TextAlign.Center
            )
        )
    }
}
