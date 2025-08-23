package com.example.icecreampos.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.example.icecreampos.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icecreampos.ui.navigation.Screen
import com.example.icecreampos.ui.viewmodel.CartViewModel
import com.example.icecreampos.ui.viewmodel.PaymentViewModel
import com.example.icecreampos.ui.viewmodel.UiState
import com.example.icecreampos.ui.viewmodel.LoginViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    totalAmount: Float,
    userRole: String,
    cartViewModel: CartViewModel = koinViewModel(),
    paymentViewModel: PaymentViewModel = koinViewModel()
) {
    val cart by cartViewModel.cart.collectAsState()
    val paymentState by paymentViewModel.paymentState.collectAsState()
    val context = LocalContext.current

    var selectedPaymentMethod by remember { mutableStateOf("Cash") }
    var amountReceived by remember { mutableStateOf("") }
    val change = (amountReceived.toDoubleOrNull() ?: 0.0) - totalAmount

    LaunchedEffect(paymentState) {
        when (val state = paymentState) {
            is UiState.Success -> {
                Toast.makeText(context, state.data, Toast.LENGTH_LONG).show()
                cartViewModel.clearCart()
                paymentViewModel.resetPaymentState()
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            }
            is UiState.Error -> {
                Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                paymentViewModel.resetPaymentState()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complete Payment") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Total to Pay", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "$${"%.2f".format(totalAmount)}",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text("Select Payment Method", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PaymentMethodButton("Cash", selectedPaymentMethod) { selectedPaymentMethod = "Cash" }
                PaymentMethodButton("Yape", selectedPaymentMethod) { selectedPaymentMethod = "Yape" }
            }

            if (selectedPaymentMethod == "Cash") {
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = amountReceived,
                    onValueChange = { amountReceived = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Amount Received") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Change:", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (change >= 0) "$${"%.2f".format(change)}" else "$0.00",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            } else if (selectedPaymentMethod == "Yape") {
                Spacer(modifier = Modifier.height(24.dp))
                Image(
                    painter = painterResource(id = R.drawable.yape_qr_code),
                    contentDescription = "Yape QR Code",
                    modifier = Modifier.size(256.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            var customerDni by remember { mutableStateOf("") }
            if (userRole == "employee" || userRole == "admin") {
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = customerDni,
                    onValueChange = { customerDni = it },
                    label = { Text("Customer DNI (Optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            Button(
                onClick = {
                    val dni = if (customerDni.isNotBlank()) customerDni else null
                    paymentViewModel.processPayment(cart, selectedPaymentMethod, dni)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                enabled = paymentState !is UiState.Loading
            ) {
                 if (paymentState is UiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("CONFIRM SALE", fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
fun PaymentMethodButton(
    text: String,
    selectedMethod: String,
    onClick: () -> Unit
) {
    val isSelected = text == selectedMethod
    Button(
        onClick = onClick,
        colors = if (isSelected) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(),
        border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder
    ) {
        Text(text)
    }
}
