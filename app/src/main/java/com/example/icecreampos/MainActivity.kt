package com.example.icecreampos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.icecreampos.ui.navigation.Screen
import com.example.icecreampos.ui.screens.*
import com.example.icecreampos.ui.theme.IceCreamPOSTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IceCreamPOSTheme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Screen.Home.route + "/{userRole}") { backStackEntry ->
            HomeScreen(
                navController = navController
            )
        }
        composable(Screen.Admin.route) {
            AdminScreen(navController = navController)
        }
        composable(Screen.ManageCategories.route) {
            CategoryManagementScreen(navController = navController)
        }
        composable(Screen.ManageProducts.route) {
            ProductManagementScreen(navController = navController)
        }
        composable(Screen.ManageToppings.route) {
            ToppingManagementScreen(navController = navController)
        }
        composable(Screen.ManageUsers.route) {
            UserManagementScreen(navController = navController)
        }
        composable(Screen.Payment.route) { backStackEntry ->
            val total = backStackEntry.arguments?.getString("totalAmount")?.toFloat() ?: 0.0f
            val userRole = backStackEntry.arguments?.getString("userRole") ?: "employee"
            PaymentScreen(
                navController = navController,
                totalAmount = total,
                userRole = userRole
            )
        }
        composable("customer_dni") {
            CustomerDNIScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
