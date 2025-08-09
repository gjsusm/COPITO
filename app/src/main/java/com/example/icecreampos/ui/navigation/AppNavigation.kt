package com.example.icecreampos.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Admin : Screen("admin")
}
