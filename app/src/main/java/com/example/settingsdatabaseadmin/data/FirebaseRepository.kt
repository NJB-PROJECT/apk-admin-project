package com.example.settingsdatabaseadmin.data

import com.example.settingsdatabaseadmin.model.AppConfig
import com.example.settingsdatabaseadmin.model.LogEntry
import com.example.settingsdatabaseadmin.model.ManagedApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    private val database = Firebase.database.reference.child("apps")

    private fun sanitizePackageName(packageName: String) = packageName.replace(".", "_")
    private fun desanitizePackageName(key: String) = key.replace("_", ".")

    private fun parseManagedAppFromSnapshot(snapshot: DataSnapshot): ManagedApp {
        val config = snapshot.child("config").getValue(AppConfig::class.java) ?: AppConfig()
        val logsData = snapshot.child("logs").getValue()
        val logs = if (logsData is List<*>) {
            logsData.filterIsinstance<HashMap<String, Any>>().map { map ->
                LogEntry(
                    timestamp = map["timestamp"] as? Long ?: 0L,
                    message = map["message"] as? String ?: ""
                )
            }
        } else {
            emptyList()
        }
        return ManagedApp(
            packageName = desanitizePackageName(snapshot.key ?: ""),
            config = config,
            logs = logs.sortedByDescending { it.timestamp }
        )
    }

    suspend fun getApps(): List<ManagedApp> {
        val snapshot = database.get().await()
        return snapshot.children.map { parseManagedAppFromSnapshot(it) }
    }

    suspend fun getApp(packageName: String): ManagedApp? {
        val sanitizedKey = sanitizePackageName(packageName)
        val snapshot = database.child(sanitizedKey).get().await()
        return if (snapshot.exists()) parseManagedAppFromSnapshot(snapshot) else null
    }

    suspend fun addApp(packageName: String) {
        val sanitizedKey = sanitizePackageName(packageName)
        val initialLog = LogEntry(message = "Application created.")
        val newAppContent = mapOf(
            "config" to AppConfig(),
            "logs" to listOf(initialLog)
        )
        database.child(sanitizedKey).setValue(newAppContent).await()
    }

    suspend fun updateAppConfig(packageName: String, newConfig: AppConfig) {
        val sanitizedKey = sanitizePackageName(packageName)
        database.child(sanitizedKey).child("config").setValue(newConfig).await()
    }

    suspend fun deleteApp(packageName: String) {
        val sanitizedKey = sanitizePackageName(packageName)
        database.child(sanitizedKey).removeValue().await()
    }

    suspend fun addLog(packageName: String, message: String) {
        val sanitizedKey = sanitizePackageName(packageName)
        val logRef = database.child(sanitizedKey).child("logs")
        val newLog = LogEntry(message = message)

        val snapshot = logRef.get().await()
        val currentLogs = if (snapshot.exists() && snapshot.value is List<*>) {
            (snapshot.value as List<*>).filterIsInstance<HashMap<String, Any>>().map { map ->
                LogEntry(
                    timestamp = map["timestamp"] as? Long ?: 0L,
                    message = map["message"] as? String ?: ""
                )
            }.toMutableList()
        } else {
            mutableListOf()
        }

        currentLogs.add(0, newLog)
        val updatedLogs = currentLogs.take(10) // Keep only the last 10 logs
        logRef.setValue(updatedLogs).await()
    }
}
