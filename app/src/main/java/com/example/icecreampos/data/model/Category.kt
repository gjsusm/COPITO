package com.example.icecreampos.data.model

import com.google.firebase.firestore.DocumentId

data class Category(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val order: Int = 0,
    val visible: Boolean = true
)
