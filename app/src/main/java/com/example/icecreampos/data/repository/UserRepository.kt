package com.example.icecreampos.data.repository

import com.example.icecreampos.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    suspend fun getUser(uid: String): User? {
        return try {
            val document = usersCollection.document(uid).get().await()
            document.toObject(User::class.java)
        } catch (e: Exception) {
            // In a real app, log this error
            null
        }
    }

    fun getUsersStream(): Flow<List<User>> {
        return usersCollection
            .snapshots()
            .map { snapshot -> snapshot.toObjects<User>() }
    }

    suspend fun updateUser(user: User) {
        usersCollection.document(user.uid).set(user).await()
    }

    suspend fun deleteUser(userId: String) {
        // This should call a Cloud Function to delete the user from Auth and Firestore
        // For now, we'll just delete the Firestore document for UI purposes
        usersCollection.document(userId).delete().await()
    }
}
