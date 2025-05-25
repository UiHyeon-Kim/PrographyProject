package com.hanpro.prographyproject.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hanpro.prographyproject.ui.navigation.NavigationItem
import com.hanpro.prographyproject.ui.theme.PrographyProjectTheme

// TODO: NavHostController를 분리하기
@Composable
fun BottomNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    val navItems = listOf(
        NavigationItem.Home,
        NavigationItem.RandomPhoto
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier.height(64.dp),
        containerColor = Color(0xFF222222),
    ) {
        Spacer(modifier = Modifier.width(32.dp))

        navItems.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = item.icon),
                        contentDescription = item.title,
                        tint = if (isSelected) Color.White else Color.Gray,
                        modifier = Modifier.size(26.dp)
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

        Spacer(modifier = Modifier.width(32.dp))
    }
}

@Preview
@Composable
fun BottomBarPreview() {
    val navController = rememberNavController()
    PrographyProjectTheme {
        BottomNavigation(navController)
    }
}