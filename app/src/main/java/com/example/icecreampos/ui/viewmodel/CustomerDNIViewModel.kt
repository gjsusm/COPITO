package com.example.icecreampos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icecreampos.data.repository.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CustomerDNIViewModel(private val customerRepository: CustomerRepository) : ViewModel() {

    private val _customerUiState = MutableStateFlow<CustomerUiState>(CustomerUiState.Idle)
    val customerUiState: StateFlow<CustomerUiState> = _customerUiState

    fun findOrCreateCustomer(dni: String) {
        viewModelScope.launch {
            _customerUiState.value = CustomerUiState.Loading
            try {
                val customer = customerRepository.findOrCreateCustomerByDni(dni)
                _customerUiState.value = CustomerUiState.Success(customer.id)
            } catch (e: Exception) {
                _customerUiState.value = CustomerUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}

sealed class CustomerUiState {
    object Idle : CustomerUiState()
    object Loading : CustomerUiState()
    data class Success(val customerId: String) : CustomerUiState()
    data class Error(val message: String) : CustomerUiState()
}
