package com.example.icecreampos.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.icecreampos.data.model.Cart
import com.example.icecreampos.data.model.OrderItem
import com.example.icecreampos.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CartViewModel : ViewModel() {

    private val _cart = MutableStateFlow(Cart())
    val cart: StateFlow<Cart> = _cart.asStateFlow()

    fun addOrderItem(orderItem: OrderItem) {
        _cart.update { currentCart ->
            // Always adds as a new line item
            val newItems = currentCart.items + orderItem
            currentCart.copy(items = newItems)
        }
    }

    fun removeItem(orderItemId: String) {
        _cart.update { currentCart ->
            val newItems = currentCart.items.filter { it.id != orderItemId }
            currentCart.copy(items = newItems)
        }
    }

    fun updateQuantity(orderItemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItem(orderItemId)
            return
        }
        _cart.update { currentCart ->
            val newItems = currentCart.items.map {
                if (it.id == orderItemId) {
                    it.copy(quantity = newQuantity)
                } else {
                    it
                }
            }
            currentCart.copy(items = newItems)
        }
    }

    fun clearCart() {
        _cart.value = Cart()
    }
}
