package com.example.settingsdatabaseadmin.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.settingsdatabaseadmin.data.FirebaseRepository
import com.example.settingsdatabaseadmin.model.ManagedApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    private val _apps = MutableStateFlow<List<ManagedApp>>(emptyList())
    val apps: StateFlow<List<ManagedApp>> = _apps

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            _apps.value = repository.getApps()
        }
    }

    fun addApp(packageName: String) {
        viewModelScope.launch {
            repository.addApp(packageName)
            loadApps()
        }
    }
}