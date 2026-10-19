package com.bmu.lifelink

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bmu.lifelink.ui.components.LifeLinkBottomBar
import com.bmu.lifelink.ui.components.LifeLinkTopBar
import com.bmu.lifelink.ui.navigation.Screen
import com.bmu.lifelink.ui.screens.about.AboutScreen
import com.bmu.lifelink.ui.screens.admin.AdminScreen
import com.bmu.lifelink.ui.screens.blog.ArticleDetailScreen
import com.bmu.lifelink.ui.screens.blog.BlogArchiveScreen
import com.bmu.lifelink.ui.screens.donor.DonorApplicationScreen
import com.bmu.lifelink.ui.screens.home.HomeScreen
import com.bmu.lifelink.ui.screens.request.RequestBloodScreen
import com.bmu.lifelink.ui.theme.LifeLinkTheme

@Composable
fun LifeLinkApp(
    navController: NavHostController = rememberNavController()
) {
    LifeLinkTheme {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

        val isPublicMainRoute = currentRoute in Screen.bottomNavItems.map { it.route }
        val isArticleDetail = currentRoute.startsWith("article/")
        val isAdminRoute = currentRoute == Screen.Admin.route

        Scaffold(
            topBar = {
                LifeLinkTopBar(
                    title = when {
                        isAdminRoute -> "Admin console"
                        isArticleDetail -> "Story"
                        else -> null
                    },
                    canNavigateBack = !isPublicMainRoute,
                    onNavigateBack = { navController.popBackStack() },
                    onAdminClick = if (!isAdminRoute) {
                        { navController.navigate(Screen.Admin.route) }
                    } else null
                )
            },
            bottomBar = {
                if (isPublicMainRoute) {
                    LifeLinkBottomBar(
                        currentRoute = currentRoute,
                        onNavigate = { screen ->
                            navController.navigate(screen.route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        onRequestBlood = { navController.navigate(Screen.Request.route) },
                        onBeDonor = { navController.navigate(Screen.Donor.route) },
                        onExploreBlog = { navController.navigate(Screen.Blog.route) },
                        onSelectStory = { id -> navController.navigate(Screen.ArticleDetail.createRoute(id)) },
                        onAdminClick = { navController.navigate(Screen.Admin.route) }
                    )
                }

                composable(Screen.About.route) {
                    AboutScreen(
                        onRequestBlood = { navController.navigate(Screen.Request.route) },
                        onAdminClick = { navController.navigate(Screen.Admin.route) }
                    )
                }

                composable(Screen.Blog.route) {
                    BlogArchiveScreen(
                        onSelectStory = { id -> navController.navigate(Screen.ArticleDetail.createRoute(id)) }
                    )
                }

                composable(Screen.Request.route) {
                    RequestBloodScreen()
                }

                composable(Screen.Donor.route) {
                    DonorApplicationScreen()
                }

                composable(Screen.Admin.route) {
                    AdminScreen()
                }

                composable(
                    route = Screen.ArticleDetail.route,
                    arguments = listOf(navArgument("articleId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val articleId = backStackEntry.arguments?.getString("articleId") ?: ""
                    ArticleDetailScreen(
                        articleId = articleId,
                        onBack = { navController.popBackStack() },
                        onOfferToDonate = { navController.navigate(Screen.Donor.route) }
                    )
                }
            }
        }
    }
}
