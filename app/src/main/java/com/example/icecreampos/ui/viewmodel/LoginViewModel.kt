package com.example.icecreampos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icecreampos.data.model.User
import com.example.icecreampos.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import com.example.icecreampos.data.SessionManager

class LoginViewModel(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun login() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            if (_email.value.isBlank() || _password.value.isBlank()) {
                _loginState.value = LoginState.Error("Email and password cannot be empty.")
                return@launch
            }
            try {
                val authResult = auth.signInWithEmailAndPassword(_email.value.trim(), _password.value).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val user = userRepository.getUser(firebaseUser.uid)
                    if (user != null) {
                        sessionManager.currentUser = user
                        _loginState.value = LoginState.Success(user)
                    } else {
                        _loginState.value = LoginState.Error("User data not found in Firestore.")
                        auth.signOut() // Sign out if user data is missing
                    }
                } else {
                    _loginState.value = LoginState.Error("Authentication failed.")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "An unknown error occurred.")
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}
