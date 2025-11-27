package com.example.settingsdatabaseadmin.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.settingsdatabaseadmin.data.FirebaseRepository
import com.example.settingsdatabaseadmin.model.ManagedApp
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    private val _apps = MutableLiveData<List<ManagedApp>>()
    val apps: LiveData<List<ManagedApp>> = _apps

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
            loadApps() // Refresh the list after adding
        }
    }
}
