package com.hanpro.prographyproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.hanpro.prographyproject.ui.navigation.AppNavHost
import com.hanpro.prographyproject.ui.theme.PrographyProjectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrographyProjectTheme {
                val navController = rememberNavController()
                AppNavHost(navController)

                /*Scaffold(
                    topBar = { TopBar() },
                    bottomBar = { BottomNavigation(navController) }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavHost(navController)
                    }
                }*/
            }
        }
    }
}