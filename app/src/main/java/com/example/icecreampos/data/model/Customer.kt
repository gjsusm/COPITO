package com.example.icecreampos.data.model

import com.google.firebase.firestore.DocumentId

data class Customer(
    @DocumentId
    val dni: String = "",
    val points: Long = 0,
    val name: String = "" // Optional, can be added later
)
