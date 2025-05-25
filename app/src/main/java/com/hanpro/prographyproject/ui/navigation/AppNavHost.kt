package com.hanpro.prographyproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hanpro.prographyproject.ui.navigation.NavigationItem.Home
import com.hanpro.prographyproject.ui.navigation.NavigationItem.RandomPhoto
import com.hanpro.prographyproject.ui.screens.HomeScreen
import com.hanpro.prographyproject.ui.screens.RandomPhotoScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Home.route
    ) {
        composable(Home.route) { HomeScreen(navController = navController) }
        composable(RandomPhoto.route) { RandomPhotoScreen(navController = navController) }
    }
}