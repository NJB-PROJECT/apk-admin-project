package com.example.settingsdatabaseadmin.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.settingsdatabaseadmin.data.FirebaseRepository
import com.example.settingsdatabaseadmin.model.AppConfig
import com.example.settingsdatabaseadmin.model.ManagedApp
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    private val _app = MutableLiveData<ManagedApp?>()
    val app: LiveData<ManagedApp?> = _app

    fun loadApp(packageName: String) {
        viewModelScope.launch {
            _app.value = repository.getApp(packageName)
        }
    }

    fun updateConfig(packageName: String, newConfig: AppConfig) {
        viewModelScope.launch {
            repository.updateAppConfig(packageName, newConfig)
            repository.addLog(packageName, "Configuration updated via admin app.")
            loadApp(packageName) // Refresh data after update
        }
    }

    fun deleteApp(packageName: String) {
        viewModelScope.launch {
            repository.deleteApp(packageName)
        }
    }
}
