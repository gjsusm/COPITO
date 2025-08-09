package com.example.icecreampos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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
                auth.signInWithEmailAndPassword(_email.value.trim(), _password.value)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _loginState.value = LoginState.Success
                        } else {
                            _loginState.value = LoginState.Error(task.exception?.message ?: "An unknown error occurred.")
                        }
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
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
