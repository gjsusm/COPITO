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

    fun addProductToCart(product: Product) {
        _cart.update { currentCart ->
            val existingItem = currentCart.items.find { it.product.id == product.id }
            val newItems = if (existingItem != null) {
                // If item already exists, increase quantity
                currentCart.items.map {
                    if (it.product.id == product.id) {
                        it.copy(quantity = it.quantity + 1)
                    } else {
                        it
                    }
                }
            } else {
                // If item is new, add it to the list
                currentCart.items + OrderItem(product = product, quantity = 1)
            }
            currentCart.copy(items = newItems)
        }
    }

    fun removeProductFromCart(productId: String) {
        _cart.update { currentCart ->
            val newItems = currentCart.items.filter { it.product.id != productId }
            currentCart.copy(items = newItems)
        }
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeProductFromCart(productId)
            return
        }
        _cart.update { currentCart ->
            val newItems = currentCart.items.map {
                if (it.product.id == productId) {
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
