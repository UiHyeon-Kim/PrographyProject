package com.hanpro.prographyproject

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hanpro.prographyproject.ui.components.BottomNavigation
import com.hanpro.prographyproject.ui.components.PrographyTopBar
import com.hanpro.prographyproject.ui.navigation.NavItem.Home
import com.hanpro.prographyproject.ui.navigation.NavItem.Random
import com.hanpro.prographyproject.ui.screens.HomeScreen
import com.hanpro.prographyproject.ui.screens.RandomPhotoScreen

@Composable
fun PrographyApp(navController: NavHostController = rememberNavController()) {
    Scaffold(
        topBar = { PrographyTopBar() },
        bottomBar = { BottomNavigation(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Home.route) { HomeScreen() }
            composable(Random.route) { RandomPhotoScreen() }
        }
    }
}



