package com.example.icecreampos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icecreampos.data.model.Cart
import com.example.icecreampos.data.model.Order
import com.example.icecreampos.data.repository.OrderRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel : ViewModel() {

    private val repository = OrderRepository()

    private val _paymentState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val paymentState = _paymentState.asStateFlow()

    fun processPayment(cart: Cart, paymentMethod: String) {
        viewModelScope.launch {
            _paymentState.value = UiState.Loading
            val currentUser = Firebase.auth.currentUser
            if (currentUser == null) {
                _paymentState.value = UiState.Error("User not authenticated.")
                return@launch
            }

            val order = Order(
                userId = currentUser.uid,
                items = cart.items,
                subtotal = cart.subtotal,
                total = cart.total,
                paymentMethod = paymentMethod
            )

            try {
                repository.saveOrder(order)
                _paymentState.value = UiState.Success("Sale completed successfully!")
            } catch (e: Exception) {
                _paymentState.value = UiState.Error(e.message ?: "Failed to save order.")
            }
        }
    }

    fun resetPaymentState() {
        _paymentState.value = UiState.Idle
    }
}
