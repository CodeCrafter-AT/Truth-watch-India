package org.truthwatchindia.app.ui.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.truthwatchindia.app.ui.screens.about.AboutScreen
import org.truthwatchindia.app.ui.screens.bookmarks.BookmarksScreen
import org.truthwatchindia.app.ui.screens.category.CategoriesHubScreen
import org.truthwatchindia.app.ui.screens.category.CategoryScreen
import org.truthwatchindia.app.ui.screens.contact.ContactScreen
import org.truthwatchindia.app.ui.screens.detail.ArticleDetailScreen
import org.truthwatchindia.app.ui.screens.home.HomeScreen
import org.truthwatchindia.app.ui.screens.search.SearchScreen
import org.truthwatchindia.app.ui.screens.settings.SettingsScreen
import org.truthwatchindia.app.ui.screens.team.TeamScreen
import org.truthwatchindia.app.ui.theme.CrimsonMaroon
import org.truthwatchindia.app.ui.theme.DarkCharcoal
import org.truthwatchindia.app.ui.theme.MetadataGray

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CategoriesHub : Screen("categories_hub")
    object Search : Screen("search")
    object Bookmarks : Screen("bookmarks")
    object Settings : Screen("settings")

    object Category : Screen("category/{categoryId}/{categoryName}") {
        fun createRoute(categoryId: Long, categoryName: String): String {
            return "category/$categoryId/${Uri.encode(categoryName)}"
        }
    }

    object ArticleDetail : Screen("article/{postId}") {
        fun createRoute(postId: Long): String = "article/$postId"
    }

    object About : Screen("about")
    object Contact : Screen("contact")
    object Team : Screen("team")
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home.route, "होम", Icons.Default.Home),
    BottomNavItem(Screen.CategoriesHub.route, "श्रेणियां", Icons.Default.GridView),
    BottomNavItem(Screen.Search.route, "खोजें", Icons.Default.Search),
    BottomNavItem(Screen.Bookmarks.route, "बुकमार्क", Icons.Default.BookmarkBorder),
    BottomNavItem(Screen.Settings.route, "सेटिंग्स", Icons.Default.Settings)
)

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    initialArticleId: Long? = null
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Handle deep link / notification target navigation
    LaunchedEffect(initialArticleId) {
        if (initialArticleId != null && initialArticleId > 0) {
            navController.navigate(Screen.ArticleDetail.createRoute(initialArticleId))
        }
    }

    // Show bottom bar only on top-level screens
    val topLevelRoutes = listOf(
        Screen.Home.route,
        Screen.CategoriesHub.route,
        Screen.Search.route,
        Screen.Bookmarks.route,
        Screen.Settings.route
    )
    val shouldShowBottomBar = currentRoute in topLevelRoutes

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CrimsonMaroon,
                                selectedTextColor = CrimsonMaroon,
                                unselectedIconColor = MetadataGray,
                                unselectedTextColor = MetadataGray,
                                indicatorColor = CrimsonMaroon.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = modifier.padding(paddingValues)
        ) {
            // 1. Home Feed
            composable(Screen.Home.route) {
                HomeScreen(
                    onPostClick = { postId ->
                        navController.navigate(Screen.ArticleDetail.createRoute(postId))
                    },
                    onCategoryClick = { categoryId, categoryName ->
                        navController.navigate(Screen.Category.createRoute(categoryId, categoryName))
                    },
                    onNavigate = { destination ->
                        navController.navigate(destination)
                    }
                )
            }

            // 2. Categories Hub Grid
            composable(Screen.CategoriesHub.route) {
                CategoriesHubScreen(
                    onCategoryClick = { categoryId, categoryName ->
                        navController.navigate(Screen.Category.createRoute(categoryId, categoryName))
                    }
                )
            }

            // 3. Search Screen
            composable(Screen.Search.route) {
                SearchScreen(
                    onPostClick = { postId ->
                        navController.navigate(Screen.ArticleDetail.createRoute(postId))
                    },
                    onBack = if (navController.previousBackStackEntry != null) {
                        { navController.popBackStack() }
                    } else null
                )
            }

            // 4. Bookmarks Screen
            composable(Screen.Bookmarks.route) {
                BookmarksScreen(
                    onPostClick = { postId ->
                        navController.navigate(Screen.ArticleDetail.createRoute(postId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // 5. Settings Screen
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigate = { destination ->
                        navController.navigate(destination)
                    }
                )
            }

            // 6. Category Archive Feed
            composable(
                route = Screen.Category.route,
                arguments = listOf(
                    navArgument("categoryId") { type = NavType.LongType },
                    navArgument("categoryName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getLong("categoryId") ?: 0L
                val categoryName = backStackEntry.arguments?.getString("categoryName").orEmpty()
                CategoryScreen(
                    categoryId = categoryId,
                    categoryName = categoryName,
                    onPostClick = { postId ->
                        navController.navigate(Screen.ArticleDetail.createRoute(postId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // 7. Single Article Detail Screen
            composable(
                route = Screen.ArticleDetail.route,
                arguments = listOf(
                    navArgument("postId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getLong("postId") ?: 0L
                ArticleDetailScreen(
                    postId = postId,
                    onBack = { navController.popBackStack() }
                )
            }

            // 8. Static Pages
            composable(Screen.About.route) {
                AboutScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Contact.route) {
                ContactScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Team.route) {
                TeamScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
