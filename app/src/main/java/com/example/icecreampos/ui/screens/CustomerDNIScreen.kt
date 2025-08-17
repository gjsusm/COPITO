package com.example.icecreampos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.icecreampos.ui.viewmodel.CustomerDNIViewModel
import com.example.icecreampos.ui.viewmodel.CustomerUiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun CustomerDNIScreen(
    navController: NavController,
    viewModel: CustomerDNIViewModel = koinViewModel()
) {
    var dni by remember { mutableStateOf("") }
    val uiState by viewModel.customerUiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is CustomerUiState.Success) {
            val customerId = (uiState as CustomerUiState.Success).customerId
            navController.navigate("home/customer/$customerId") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Enter your DNI", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = dni,
            onValueChange = { dni = it },
            label = { Text("DNI") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (dni.isNotBlank()) {
                    viewModel.findOrCreateCustomer(dni)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is CustomerUiState.Loading
        ) {
            if (uiState is CustomerUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Continue")
            }
        }

        if (uiState is CustomerUiState.Error) {
            Text(
                text = (uiState as CustomerUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
