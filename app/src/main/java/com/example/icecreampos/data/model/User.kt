package com.example.icecreampos.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "employee", // "employee" or "admin"
    val active: Boolean = true,
    @ServerTimestamp
    val createdAt: Date? = null
)
