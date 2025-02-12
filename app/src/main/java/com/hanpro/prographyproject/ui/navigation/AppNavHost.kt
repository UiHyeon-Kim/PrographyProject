package com.hanpro.prographyproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hanpro.prographyproject.ui.HomeScreen
import com.hanpro.prographyproject.ui.PhotoDetailScreen
import com.hanpro.prographyproject.ui.RandomPhotoScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationItem.Home.route
    ) {
        composable(NavigationItem.Home.route) { HomeScreen(navController) }
        composable(NavigationItem.RandomPhoto.route) { RandomPhotoScreen(navController) }
        composable("photoDetail") { PhotoDetailScreen() }
    }
}