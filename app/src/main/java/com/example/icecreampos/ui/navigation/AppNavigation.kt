package com.example.icecreampos.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Admin : Screen("admin")
    object ManageCategories : Screen("manage_categories")
    object ManageProducts : Screen("manage_products")
    object ManageToppings : Screen("manage_toppings")
    object ManageUsers : Screen("manage_users")
    object Payment : Screen("payment")
}
