package com.example.icecreampos.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.icecreampos.ui.viewmodel.SettingsViewModel
import com.example.icecreampos.ui.viewmodel.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uploadQrCode(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Upload Yape QR Code")
        }
        Spacer(modifier = Modifier.height(16.dp))
        when (uiState) {
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.Success -> {
                Text((uiState as UiState.Success<String>).data)
            }
            is UiState.Error -> {
                Text((uiState as UiState.Error).message, color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}
