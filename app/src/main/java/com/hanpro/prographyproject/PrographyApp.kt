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

/**
 * 상단 바와 하단 내비게이션을 포함한 앱의 최상위 Compose 구성으로 네비게이션 호스트를 제공한다.
 *
 * 내부에서 전달된 NavHostController를 사용해 시작 화면(Home)과 랜덤 사진 화면(Random)으로의 라우트를 설정한다.
 *
 * @param navController 앱 전체의 화면 전환을 관리하는 NavHostController. 기본값은 rememberNavController()이다.
 */
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


