package com.hanpro.prographyproject.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.hanpro.prographyproject.ui.navigation.NavItem
import com.hanpro.prographyproject.ui.theme.PrographyProjectTheme

/**
 * 화면 하단에 홈 및 랜덤 항목을 가진 내비게이션 바를 렌더링한다.
 *
 * 선택된 항목은 현재 라우트와 일치할 때 시각적으로 강조되며, 항목 탭 시 해당 라우트로 네비게이트한다.
 *
 * @param navController 네비게이션 상태 및 라우팅을 관리하는 NavHostController.
 * @param modifier 레이아웃이나 스타일을 조정하기 위한 Modifier.
 */
@Composable
fun BottomNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    val navItems = listOf(
        NavItem.Home,
        NavItem.Random
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier.height(64.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Spacer(modifier = Modifier.width(32.dp))

        navItems.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = screen.icon!!),
                        contentDescription = screen.title,
                        tint = if (isSelected) Color.White else Color.Gray,
                        modifier = Modifier.size(26.dp)
                    )
                },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(screen.route) {
                            popUpTo(currentRoute!!) { inclusive = true }
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