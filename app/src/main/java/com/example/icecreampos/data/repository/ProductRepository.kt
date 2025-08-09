package com.example.icecreampos.data.repository

import android.net.Uri
import com.example.icecreampos.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val productsCollection = db.collection("products")

    // Get a real-time stream of products for a specific category
    fun getProductsStream(categoryId: String): Flow<List<Product>> {
        return productsCollection
            .whereEqualTo("categoryId", categoryId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects<Product>()
            }
    }

    suspend fun addProduct(product: Product): String {
        val documentReference = productsCollection.add(product).await()
        return documentReference.id
    }

    suspend fun updateProduct(product: Product) {
        productsCollection.document(product.id).set(product).await()
    }

    suspend fun deleteProduct(productId: String) {
        productsCollection.document(productId).delete().await()
    }

    suspend fun uploadProductImage(productId: String, imageUri: Uri): String {
        val imageRef = storage.reference.child("products/$productId.jpg")
        imageRef.putFile(imageUri).await()
        return imageRef.downloadUrl.await().toString()
    }
}
