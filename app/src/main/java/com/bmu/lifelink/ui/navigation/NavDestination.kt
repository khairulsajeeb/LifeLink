package com.bmu.lifelink.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object About : Screen("about", "About", Icons.Default.Info)
    object Blog : Screen("blog", "Blog", Icons.Default.Article)
    object Request : Screen("request", "Request", Icons.Default.Favorite)
    object Donor : Screen("donor", "Be a donor", Icons.Default.VolunteerActivism)
    object Admin : Screen("admin", "Admin")
    object ArticleDetail : Screen("article/{articleId}", "Article") {
        fun createRoute(articleId: String) = "article/$articleId"
    }

    companion object {
        val bottomNavItems = listOf(Home, About, Blog, Request, Donor)
    }
}
