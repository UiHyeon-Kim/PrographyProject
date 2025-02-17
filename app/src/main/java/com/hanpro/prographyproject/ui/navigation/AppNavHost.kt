package com.hanpro.prographyproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hanpro.prographyproject.ui.screens.HomeScreen
import com.hanpro.prographyproject.ui.screens.PhotoDetailScreen
import com.hanpro.prographyproject.ui.screens.RandomPhotoScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationItem.Home.route
    ) {
        composable(NavigationItem.Home.route) { HomeScreen(navController = navController) }

        composable(NavigationItem.RandomPhoto.route) {
            RandomPhotoScreen(onBookmarkAdd = {}, onLoadMore = {})
        }

        composable(
            "photoDetail/{photoId}",
            arguments = listOf(navArgument("photoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getString("photoId") ?: ""
            PhotoDetailScreen(
                photoId = photoId,
                onClose = { navController.popBackStack() }
            )
        }
    }
}