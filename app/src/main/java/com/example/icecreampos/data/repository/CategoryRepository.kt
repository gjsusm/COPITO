package com.example.icecreampos.data.repository

import com.example.icecreampos.data.model.Category
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class CategoryRepository {
    private val db = FirebaseFirestore.getInstance()
    private val categoriesCollection = db.collection("categories")

    fun getCategoriesStream(): Flow<List<Category>> {
        return categoriesCollection
            .orderBy("order")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects<Category>()
            }
    }

    suspend fun addCategory(category: Category) {
        // Firestore will auto-generate an ID
        categoriesCollection.add(category).await()
    }

    suspend fun updateCategory(category: Category) {
        categoriesCollection.document(category.id).set(category).await()
    }

    suspend fun deleteCategory(categoryId: String) {
        categoriesCollection.document(categoryId).delete().await()
    }
}
