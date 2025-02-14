package com.hanpro.prographyproject.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hanpro.prographyproject.ui.navigation.NavigationItem

@Composable
fun BottomNavigation(navController: NavHostController) {
    val navItems = listOf(
        NavigationItem.Home,
        NavigationItem.RandomPhoto
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val systemIoController = rememberSystemUiController()
    systemIoController.setNavigationBarColor(Color(0xFF222222))
    systemIoController.setStatusBarColor(
        color = Color(0x00000000),
        darkIcons = true
    )

    // TODO: 아이콘 간 간격 줄이는 방법 찾기
    NavigationBar(
        containerColor = Color(0xFF222222)
    ) {
        navItems.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = item.icon),
                        contentDescription = item.title,
                        tint = if (isSelected) Color.White else Color.Gray,
                    )
                },
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}