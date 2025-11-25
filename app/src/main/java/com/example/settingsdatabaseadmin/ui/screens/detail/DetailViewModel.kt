package com.example.settingsdatabaseadmin.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.settingsdatabaseadmin.data.FirebaseRepository
import com.example.settingsdatabaseadmin.model.AppConfig
import com.example.settingsdatabaseadmin.model.ManagedApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    private val _app = MutableStateFlow<ManagedApp?>(null)
    val app: StateFlow<ManagedApp?> = _app

    fun loadApp(packageName: String) {
        viewModelScope.launch {
            _app.value = repository.getApp(packageName)
        }
    }

    fun updateConfig(packageName: String, newConfig: AppConfig, logMessage: String) {
        viewModelScope.launch {
            repository.updateAppConfig(packageName, newConfig)
            repository.addLog(packageName, logMessage)
            loadApp(packageName)
        }
    }

    fun deleteApp(packageName: String) {
        viewModelScope.launch {
            repository.deleteApp(packageName)
        }
    }
}