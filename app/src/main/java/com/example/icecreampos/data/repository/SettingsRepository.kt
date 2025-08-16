package com.example.icecreampos.data.repository

import com.example.icecreampos.data.model.Settings
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SettingsRepository {
    private val db = FirebaseFirestore.getInstance()
    // The settings will be stored in a single document with a known ID.
    private val settingsDocRef = db.collection("settings").document("config")

    suspend fun getSettings(): Settings? {
        return try {
            val document = settingsDocRef.get().await()
            document.toObject(Settings::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveSettings(settings: Settings) {
        settingsDocRef.set(settings).await()
    }
}
