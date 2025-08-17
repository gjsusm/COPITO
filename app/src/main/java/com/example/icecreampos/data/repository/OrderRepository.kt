package com.example.icecreampos.data.repository

import com.example.icecreampos.data.model.Order
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class OrderRepository {
    private val db = FirebaseFirestore.getInstance()
    private val ordersCollection = db.collection("orders")

    suspend fun saveOrder(order: Order) {
        ordersCollection.add(order).await()
    }
}
