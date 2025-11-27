package com.example.settingsdatabaseadmin.model

data class ManagedApp(
    val packageName: String = "",
    val config: AppConfig = AppConfig(),
    val logs: List<LogEntry> = emptyList()
) {
    // No-argument constructor for Firebase deserialization
    constructor() : this("", AppConfig(), emptyList())
}
