package com.hanpro.prographyproject.ui.navigation

sealed class NavigationItem(val route: String) {
    data object Home : NavigationItem("home")
    data object RandomPhoto : NavigationItem("randomPhoto")
}