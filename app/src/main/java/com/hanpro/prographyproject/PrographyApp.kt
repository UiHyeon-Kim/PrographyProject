package com.hanpro.prographyproject

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.hanpro.prographyproject.ui.components.BottomNavigation
import com.hanpro.prographyproject.ui.components.PrographyTopBar
import com.hanpro.prographyproject.ui.dialog.PhotoDetailDialog
import com.hanpro.prographyproject.ui.navigation.AppNavigation
import com.hanpro.prographyproject.ui.navigation.NavigationItem.DetailPhoto
import com.hanpro.prographyproject.ui.navigation.NavigationItem.Home
import com.hanpro.prographyproject.ui.navigation.NavigationItem.RandomPhoto
import com.hanpro.prographyproject.ui.screens.HomeScreen
import com.hanpro.prographyproject.ui.screens.RandomPhotoScreen

@Composable
fun PrographyApp() {
    val navController = rememberNavController()
    val appNavigation = remember(navController) { AppNavigation(navController) }

    CompositionLocalProvider(LocalNavigation provides appNavigation) {
        Scaffold(
            topBar = { PrographyTopBar() },
            bottomBar = { BottomNavigation(navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                // TODO: 컨트롤러 대신 이동 함수 넣기
                composable(Home.route) { HomeScreen() }
                composable(RandomPhoto.route) { RandomPhotoScreen() }
                dialog(DetailPhoto.route) { PhotoDetailDialog(onClose = {}) }
            }
        }
    }
}

val LocalNavigation = staticCompositionLocalOf<AppNavigation?> { null }

