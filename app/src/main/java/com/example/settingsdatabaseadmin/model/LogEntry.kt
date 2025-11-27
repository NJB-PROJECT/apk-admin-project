package com.example.settingsdatabaseadmin.model

data class LogEntry(
    val timestamp: Long = System.currentTimeMillis(),
    val message: String = ""
) {
    // No-argument constructor for Firebase deserialization
    constructor() : this(0, "")
}
