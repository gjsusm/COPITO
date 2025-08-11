package com.example.icecreampos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icecreampos.data.model.User
import com.example.icecreampos.data.repository.UserRepository
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val repository = UserRepository()
    private val functions = Firebase.functions

    val users: StateFlow<List<User>> = repository.getUsersStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createUser(name: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            val data = hashMapOf(
                "name" to name,
                "email" to email,
                "password" to password,
                "role" to role
            )
            try {
                functions.getHttpsCallable("createNewUser").call(data).await()
                // Optionally handle success, e.g., show a success message
            } catch (e: Exception) {
                // Optionally handle error, e.g., show an error message
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            repository.deleteUser(userId)
        }
    }
}
