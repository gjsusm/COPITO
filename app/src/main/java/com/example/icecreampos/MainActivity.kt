package com.example.icecreampos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.icecreampos.ui.navigation.Screen
import com.example.icecreampos.ui.screens.AdminScreen
import com.example.icecreampos.ui.screens.HomeScreen
import com.example.icecreampos.ui.screens.LoginScreen
import com.example.icecreampos.ui.theme.IceCreamPOSTheme

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
                    NavHost(navController = navController, startDestination = Screen.Login.route) {
                        composable(Screen.Login.route) {
                            LoginScreen(navController)
                        }
                        composable(Screen.Home.route) {
                            HomeScreen(navController)
                        }
                        composable(Screen.Admin.route) {
                            AdminScreen(navController)
                        }
                    }
                }
            }
        }
    }
}
