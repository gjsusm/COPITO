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

import com.example.icecreampos.data.repository.CustomerRepository

class PaymentViewModel(
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _paymentState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val paymentState = _paymentState.asStateFlow()

    fun processPayment(cart: Cart, paymentMethod: String, customerDni: String? = null) {
        viewModelScope.launch {
            _paymentState.value = UiState.Loading
            val currentUser = Firebase.auth.currentUser
            if (currentUser == null && customerDni == null) {
                _paymentState.value = UiState.Error("User not authenticated.")
                return@launch
            }

            val customer = if (customerDni != null) {
                customerRepository.findOrCreateCustomerByDni(customerDni)
            } else {
                null
            }

            val order = Order(
                userId = currentUser?.uid ?: "",
                customerId = customer?.id,
                items = cart.items,
                subtotal = cart.subtotal,
                total = cart.total,
                paymentMethod = paymentMethod
            )

            try {
                orderRepository.saveOrder(order)
                if (customer != null) {
                    val points = cart.total.toInt()
                    customerRepository.addPointsToCustomer(customer.id, points)
                }
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
