package com.example.icecreampos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.icecreampos.ui.navigation.Screen
import com.example.icecreampos.ui.screens.AdminScreen
import com.example.icecreampos.ui.screens.HomeScreen
import com.example.icecreampos.ui.screens.LoginScreen
import com.example.icecreampos.ui.screens.PaymentScreen
import com.example.icecreampos.ui.screens.management.CategoryManagementScreen
import com.example.icecreampos.ui.screens.management.ProductManagementScreen
import com.example.icecreampos.ui.screens.management.SettingsScreen
import com.example.icecreampos.ui.screens.management.ToppingManagementScreen
import com.example.icecreampos.ui.screens.management.UserManagementScreen
import com.example.icecreampos.ui.theme.IceCreamPOSTheme
import com.example.icecreampos.ui.viewmodel.CartViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IceCreamPOSTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val cartViewModel: CartViewModel = viewModel()

                    NavHost(navController = navController, startDestination = Screen.Login.route) {
                        composable(Screen.Login.route) {
                            LoginScreen(navController)
                        }
                        composable(
                            route = Screen.Home.route + "/{isAdmin}",
                            arguments = listOf(navArgument("isAdmin") { type = NavType.BoolType })
                        ) { backStackEntry ->
                            val isAdmin = backStackEntry.arguments?.getBoolean("isAdmin") ?: false
                            HomeScreen(navController, isAdmin, cartViewModel = cartViewModel)
                        }
                        composable(Screen.Admin.route) {
                            AdminScreen(navController)
                        }
                        composable(Screen.ManageCategories.route) {
                            CategoryManagementScreen(navController)
                        }
                        composable(Screen.ManageProducts.route) {
                            ProductManagementScreen(navController)
                        }
                        composable(Screen.ManageToppings.route) {
                            ToppingManagementScreen(navController)
                        }
                        composable(Screen.ManageUsers.route) {
                            UserManagementScreen(navController)
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(navController)
                        }
                        composable(
                            route = "payment/{totalAmount}",
                            arguments = listOf(navArgument("totalAmount") { type = NavType.FloatType })
                        ) { backStackEntry ->
                            val totalAmount = backStackEntry.arguments?.getFloat("totalAmount") ?: 0f
                            PaymentScreen(navController, totalAmount, cartViewModel = cartViewModel)
                        }
                    }
                }
            }
        }
    }
}
