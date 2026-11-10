package org.truthwatchindia.app.ui.screens.home

import android.content.Intent
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.truthwatchindia.app.data.model.WpPost
import org.truthwatchindia.app.ui.components.BreakingNewsTicker
import org.truthwatchindia.app.ui.components.CategoryTabStrip
import org.truthwatchindia.app.ui.components.CompactArticleRow
import org.truthwatchindia.app.ui.components.ErrorMessage
import org.truthwatchindia.app.ui.components.FeaturedArticleCard
import org.truthwatchindia.app.ui.components.LoadingIndicator
import org.truthwatchindia.app.ui.components.SectionHeader
import org.truthwatchindia.app.ui.theme.AccentBlue
import org.truthwatchindia.app.ui.theme.CanvasWhite
import org.truthwatchindia.app.ui.theme.CrimsonMaroon
import org.truthwatchindia.app.ui.theme.DarkCharcoal
import org.truthwatchindia.app.ui.theme.DividerStroke
import org.truthwatchindia.app.ui.theme.MetadataGray
import org.truthwatchindia.app.ui.theme.SurfaceLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPostClick: (Long) -> Unit,
    onCategoryClick: (Long, String) -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory())
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                // Top Branding Strip with Crimson Header
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Logo Box
                            Box(
                                modifier = Modifier
                                    .background(Color.White, RoundedCornerShape(3.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "TWI",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = CrimsonMaroon
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Truth Watch India",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "सत्यमेव जयते • लखनऊ",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    },
                    actions = {
                        // Search Action Icon
                        IconButton(onClick = { onNavigate("search") }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White
                            )
                        }

                        // Bookmark Action Icon
                        IconButton(onClick = { onNavigate("bookmarks") }) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmarks",
                                tint = Color.White
                            )
                        }

                        // Share Action Icon
                        IconButton(onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Truth Watch India News App: https://truthwatchindia.org"
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share App"))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }

                        // Overflow 3-dot Menu
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("हमारे बारे में (About Us)") },
                                onClick = {
                                    showMenu = false
                                    onNavigate("about")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("हमारी टीम (Our Team)") },
                                onClick = {
                                    showMenu = false
                                    onNavigate("team")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("संपर्क करें (Contact Us)") },
                                onClick = {
                                    showMenu = false
                                    onNavigate("contact")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("सेटिंग्स (Settings)") },
                                onClick = {
                                    showMenu = false
                                    onNavigate("settings")
                                }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = CrimsonMaroon,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )

                // Category Navigation Bar
                CategoryTabStrip(
                    categories = state.categories,
                    selectedCategoryId = null,
                    onCategorySelected = { id, name ->
                        if (id != null) {
                            onCategoryClick(id, name)
                        }
                    }
                )
            }
        },
        containerColor = SurfaceLight
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    LoadingIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.errorMessage != null && state.featuredPost == null -> {
                    ErrorMessage(
                        message = state.errorMessage ?: "",
                        onRetry = { viewModel.refresh() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Offline notice banner if viewing cached content
                        if (state.isOffline) {
                            item {
                                Surface(
                                    color = Color(0xFFFFF3CD),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CloudOff,
                                            contentDescription = null,
                                            tint = Color(0xFF856404),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "ऑफ़लाइन मोड: पहले से सहेजा गया समाचार दिखाया जा रहा है।",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFF856404),
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // 1. Breaking News Ticker (ब्रेकिंग न्यूज़)
                        if (state.breakingHeadlines.isNotEmpty()) {
                            item {
                                BreakingNewsTicker(
                                    headlines = state.breakingHeadlines,
                                    onHeadlineClick = { index ->
                                        state.featuredPost?.let { onPostClick(it.id) }
                                    }
                                )
                            }
                        }

                        // 2. Featured Editorial Story (Lead Headline)
                        state.featuredPost?.let { featured ->
                            item {
                                FeaturedArticleCard(
                                    post = featured,
                                    onClick = { onPostClick(featured.id) }
                                )
                            }
                        }

                        // 3. Category Rail: उत्तर प्रदेश (UP News, Category ID 7)
                        if (state.upPosts.isNotEmpty()) {
                            item {
                                EditorialRailSection(
                                    title = "उत्तर प्रदेश (UP News)",
                                    posts = state.upPosts,
                                    onPostClick = onPostClick,
                                    onViewAll = { onCategoryClick(7L, "उत्तर प्रदेश") }
                                )
                            }
                        }

                        // 4. Category Rail: लखनऊ (Lucknow News, Category ID 3)
                        if (state.lucknowPosts.isNotEmpty()) {
                            item {
                                EditorialRailSection(
                                    title = "लखनऊ (Lucknow News)",
                                    posts = state.lucknowPosts,
                                    onPostClick = onPostClick,
                                    onViewAll = { onCategoryClick(3L, "लखनऊ") }
                                )
                            }
                        }

                        // 5. Category Rail: देश (National News, Category ID 5)
                        if (state.nationalPosts.isNotEmpty()) {
                            item {
                                EditorialRailSection(
                                    title = "देश (National News)",
                                    posts = state.nationalPosts,
                                    onPostClick = onPostClick,
                                    onViewAll = { onCategoryClick(5L, "देश") }
                                )
                            }
                        }

                        // 6. Latest Stories Section (ताज़ा समाचार)
                        if (state.latestPosts.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "ताज़ा समाचार (Latest Stories)"
                                )
                            }

                            items(
                                items = state.latestPosts,
                                key = { it.id }
                            ) { post ->
                                CompactArticleRow(
                                    post = post,
                                    onClick = { onPostClick(post.id) }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditorialRailSection(
    title: String,
    posts: List<WpPost>,
    onPostClick: (Long) -> Unit,
    onViewAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Rail Ribbon Header with View All action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(width = 4.dp, height = 18.dp)
                        .background(CrimsonMaroon, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal,
                        fontSize = 17.sp
                    )
                )
            }

            Row(
                modifier = Modifier
                    .clickable(onClick = onViewAll)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "सभी देखें",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CrimsonMaroon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = CrimsonMaroon,
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        HorizontalDivider(
            thickness = 1.5.dp,
            color = CrimsonMaroon.copy(alpha = 0.2f),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
        )

        // Lead story in this category
        val leadPost = posts.firstOrNull()
        if (leadPost != null) {
            FeaturedArticleCard(
                post = leadPost,
                onClick = { onPostClick(leadPost.id) }
            )
        }

        // Secondary thumbnail list in this category
        posts.drop(1).forEach { subPost ->
            CompactArticleRow(
                post = subPost,
                onClick = { onPostClick(subPost.id) }
            )
        }
    }
}
