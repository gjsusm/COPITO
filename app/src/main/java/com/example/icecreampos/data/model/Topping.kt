package com.example.icecreampos.data.model

import com.google.firebase.firestore.DocumentId

data class Topping(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val applicableTo: List<String> = emptyList(), // List of product IDs
    val active: Boolean = true
)
