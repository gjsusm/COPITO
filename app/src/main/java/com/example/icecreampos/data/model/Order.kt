package com.example.icecreampos.data.model

import java.util.UUID

data class OrderItem(
    val id: String = UUID.randomUUID().toString(),
    val product: Product,
    val quantity: Int = 1,
    val selectedToppings: List<Topping> = emptyList()
) {
    // Topping prices are not added, as per new requirements
    val totalPrice: Double
        get() = product.price * quantity
}

data class Cart(
    val items: List<OrderItem> = emptyList()
) {
    val subtotal: Double
        get() = items.sumOf { it.totalPrice }

    val total: Double
        get() = subtotal
}
