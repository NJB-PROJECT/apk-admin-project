package com.example.settingsdatabaseadmin.model

data class LogEntry(
    val timestamp: Long = System.currentTimeMillis(),
    val message: String = ""
)