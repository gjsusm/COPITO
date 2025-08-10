package com.example.icecreampos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.icecreampos.ui.navigation.Screen

@Composable
fun AdminScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Panel de Administración", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        AdminButton(
            text = "Gestionar Categorías",
            onClick = { navController.navigate(Screen.ManageCategories.route) }
        )
        AdminButton(
            text = "Gestionar Productos",
            onClick = { navController.navigate(Screen.ManageProducts.route) }
        )
        AdminButton(
            text = "Gestionar Toppings",
            onClick = { navController.navigate(Screen.ManageToppings.route) }
        )
        AdminButton(text = "Gestionar Usuarios", onClick = { /* TODO */ })

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Volver al TPV")
        }
    }
}

@Composable
private fun AdminButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}
