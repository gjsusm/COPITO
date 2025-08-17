package com.example.icecreampos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icecreampos.data.model.Settings
import com.example.icecreampos.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import android.net.Uri

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    private val _settings = MutableStateFlow(Settings())
    val settings = _settings.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val loadedSettings = repository.getSettings()
            if (loadedSettings != null) {
                _settings.value = loadedSettings
                _uiState.value = UiState.Success("Settings loaded")
            } else {
                _uiState.value = UiState.Error("Could not load settings.")
            }
        }
    }

    fun onSettingsChanged(newSettings: Settings) {
        _settings.value = newSettings
    }

    fun saveSettings() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                repository.saveSettings(_settings.value)
                _uiState.value = UiState.Success("Settings saved successfully!")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to save settings.")
            }
        }
    }

    fun uploadQrCode(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val downloadUrl = repository.uploadYapeQrCode(uri)
                repository.saveYapeQrCodeUrl(downloadUrl)
                _uiState.value = UiState.Success("QR Code uploaded successfully")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }
}
