package com.example.icecreampos.data.repository

import com.example.icecreampos.data.model.Settings
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

class SettingsRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    private val settingsCollection = firestore.collection("settings")
    private val qrCodeStorageRef = storage.reference.child("qrcodes/yape.jpg")

    suspend fun getSettings(): Settings? {
        return try {
            settingsCollection.document("config").get().await().toObject(Settings::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveSettings(settings: Settings) {
        settingsCollection.document("config").set(settings).await()
    }

    suspend fun uploadYapeQrCode(uri: Uri): String {
        return qrCodeStorageRef.putFile(uri).await().storage.downloadUrl.await().toString()
    }

    suspend fun saveYapeQrCodeUrl(url: String) {
        settingsCollection.document("config").update("yapeQrCodeUrl", url).await()
    }
}
