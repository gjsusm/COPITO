package com.example.icecreampos.data.repository

import com.example.icecreampos.data.models.Customer
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

import com.google.firebase.firestore.FieldValue

class CustomerRepository(private val firestore: FirebaseFirestore) {

    private val customersCollection = firestore.collection("customers")

    suspend fun findOrCreateCustomerByDni(dni: String): Customer {
        val querySnapshot = customersCollection.whereEqualTo("dni", dni).get().await()
        if (querySnapshot.isEmpty) {
            val newCustomer = Customer(dni = dni)
            val docRef = customersCollection.add(newCustomer).await()
            return newCustomer.copy(id = docRef.id)
        } else {
            val document = querySnapshot.documents.first()
            return document.toObject(Customer::class.java)!!.copy(id = document.id)
        }
    }

    suspend fun addPointsToCustomer(customerId: String, points: Int) {
        customersCollection.document(customerId).update("points", FieldValue.increment(points.toLong())).await()
    }
}
