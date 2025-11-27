package com.example.settingsdatabaseadmin.model

data class AppConfig(
    val latestVersionCode: Int = 1,
    val latestVersionName: String = "1.0.0",
    val isActive: Boolean = true,
    val maintenanceMode: Boolean = false,
    val maintenanceCode: String = "123456"
)