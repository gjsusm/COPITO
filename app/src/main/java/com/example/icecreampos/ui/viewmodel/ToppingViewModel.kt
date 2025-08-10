package com.example.icecreampos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icecreampos.data.model.Topping
import com.example.icecreampos.data.repository.ToppingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ToppingViewModel : ViewModel() {

    private val repository = ToppingRepository()

    val toppings: StateFlow<List<Topping>> = repository.getToppingsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTopping(name: String, price: Double) {
        viewModelScope.launch {
            val newTopping = Topping(name = name, price = price, active = true)
            repository.addTopping(newTopping)
        }
    }

    fun updateTopping(topping: Topping) {
        viewModelScope.launch {
            repository.updateTopping(topping)
        }
    }

    fun deleteTopping(toppingId: String) {
        viewModelScope.launch {
            repository.deleteTopping(toppingId)
        }
    }
}
