package com.hanpro.prographyproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hanpro.prographyproject.ui.screens.HomeScreen
import com.hanpro.prographyproject.ui.screens.PhotoDetailScreen
import com.hanpro.prographyproject.ui.screens.RandomPhotoScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationItem.Home.route
    ) {
        composable(NavigationItem.Home.route) { HomeScreen() }
        composable(NavigationItem.RandomPhoto.route) {
            RandomPhotoScreen(emptyList(), onBookmarkAdd = {})
        }
        composable("photoDetail") { PhotoDetailScreen() }
    }
}