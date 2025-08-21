package com.example.icecreampos.data.repository

import com.example.icecreampos.data.model.Topping
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ToppingRepository(private val db: FirebaseFirestore) {
    private val toppingsCollection = db.collection("toppings")

    fun getToppingsStream(): Flow<List<Topping>> {
        return toppingsCollection
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects<Topping>()
            }
    }

    suspend fun addTopping(topping: Topping) {
        toppingsCollection.add(topping).await()
    }

    suspend fun updateTopping(topping: Topping) {
        toppingsCollection.document(topping.id).set(topping).await()
    }

    suspend fun deleteTopping(toppingId: String) {
        toppingsCollection.document(toppingId).delete().await()
    }
}
