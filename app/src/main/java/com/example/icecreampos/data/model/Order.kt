package com.example.icecreampos.data.model

data class OrderItem(
    val product: Product,
    val quantity: Int = 1,
    // Toppings will be added later
    // val selectedToppings: List<Topping> = emptyList()
) {
    val totalPrice: Double
        get() = product.price * quantity
}

data class Cart(
    val items: List<OrderItem> = emptyList(),
    val taxRate: Double = 0.08 // Example tax rate (8%)
) {
    val subtotal: Double
        get() = items.sumOf { it.totalPrice }

    val tax: Double
        get() = subtotal * taxRate

    val total: Double
        get() = subtotal + tax
}
