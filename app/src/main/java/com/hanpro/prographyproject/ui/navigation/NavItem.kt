package com.hanpro.prographyproject.ui.navigation

import androidx.annotation.DrawableRes
import com.hanpro.prographyproject.R

sealed class NavItem(
    val route: String,
    val title: String,
    @DrawableRes val icon: Int?,
) {
    data object Home : NavItem("home", "Home", R.drawable.house)
    data object Random : NavItem("randomPhoto", "Random Photo", R.drawable.cards)
}