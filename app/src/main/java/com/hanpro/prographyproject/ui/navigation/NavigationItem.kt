package com.hanpro.prographyproject.ui.navigation

import androidx.annotation.DrawableRes
import com.hanpro.prographyproject.R

sealed class NavigationItem(
    val route: String,
    val title: String,
    @DrawableRes val icon: Int?,
) {
    data object Home : NavigationItem("home", "Home", R.drawable.house)
    data object RandomPhoto : NavigationItem("randomPhoto", "Random Photo", R.drawable.cards)
    data object DetailPhoto : NavigationItem("detailPhoto", "Detail Photo", null)
}