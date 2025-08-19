package com.example.icecreampos.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserRoleViewModel : ViewModel() {
    private val _userRole = MutableStateFlow("employee")
    val userRole: StateFlow<String> = _userRole

    fun setUserRole(role: String) {
        _userRole.value = role
    }
}
