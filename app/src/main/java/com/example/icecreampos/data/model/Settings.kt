package com.example.icecreampos.data.model

data class Settings(
    val storeName: String = "",
    val address: String = "",
    val ruc: String = "", // Tax ID
    val currency: String = "USD",
    val taxPercent: Double = 0.0
)
