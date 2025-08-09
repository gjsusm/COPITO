package com.example.icecreampos.data.model

import com.google.firebase.firestore.DocumentId

data class Product(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val categoryId: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val stock: Int? = null // Optional stock
)
