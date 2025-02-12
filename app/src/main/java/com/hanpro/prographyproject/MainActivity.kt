package com.hanpro.prographyproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hanpro.prographyproject.ui.navigation.AppNavHost
import com.hanpro.prographyproject.ui.navigation.BottomNavigation
import com.hanpro.prographyproject.ui.navigation.NavigationItem
import com.hanpro.prographyproject.ui.theme.PrographyProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrographyProjectTheme {
                val navController = rememberNavController()

                Scaffold(
                    topBar = {
                        if (showBar(navController)) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.logo),
                                contentDescription = "logo"
                            )
                        }
                    },
                    bottomBar = {
                        if (showBar(navController)) {
                            BottomNavigation(navController)
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavHost(navController)
                    }
                }
            }
        }
    }
}

@Composable
fun showBar(navController: NavHostController): Boolean {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    return currentRoute == NavigationItem.Home.route ||
            currentRoute == NavigationItem.RandomPhoto.route
}
