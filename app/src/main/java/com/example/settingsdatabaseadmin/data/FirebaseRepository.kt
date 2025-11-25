package com.example.settingsdatabaseadmin.data

import com.example.settingsdatabaseadmin.model.AppConfig
import com.example.settingsdatabaseadmin.model.LogEntry
import com.example.settingsdatabaseadmin.model.ManagedApp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    private val database = FirebaseDatabase.getInstance().reference.child("apps")

    suspend fun getApps(): List<ManagedApp> {
        val snapshot = database.get().await()
        return snapshot.children.mapNotNull {
            val config = it.child("config").getValue(AppConfig::class.java) ?: AppConfig()
            val logs = it.child("logs").children.mapNotNull { logSnapshot ->
                logSnapshot.getValue(LogEntry::class.java)
            }
            ManagedApp(
                packageName = it.key?.replace("_", ".") ?: "",
                config = config,
                logs = logs.sortedByDescending { log -> log.timestamp }
            )
        }
    }

    suspend fun getApp(packageName: String): ManagedApp? {
        val sanitizedPackageName = packageName.replace(".", "_")
        val snapshot = database.child(sanitizedPackageName).get().await()
        if (!snapshot.exists()) {
            return null
        }
        val config = snapshot.child("config").getValue(AppConfig::class.java) ?: AppConfig()
        val logs = snapshot.child("logs").children.mapNotNull { logSnapshot ->
            logSnapshot.getValue(LogEntry::class.java)
        }
        return ManagedApp(
            packageName = snapshot.key?.replace("_", ".") ?: "",
            config = config,
            logs = logs.sortedByDescending { log -> log.timestamp }
        )
    }

    suspend fun addApp(packageName: String) {
        val sanitizedPackageName = packageName.replace(".", "_")
        val newApp = ManagedApp(
            packageName = packageName,
            config = AppConfig(),
            logs = listOf(LogEntry(message = "Application created."))
        )
        database.child(sanitizedPackageName).setValue(newApp).await()
    }

    suspend fun updateAppConfig(packageName: String, newConfig: AppConfig) {
        val sanitizedPackageName = packageName.replace(".", "_")
        database.child(sanitizedPackageName).child("config").setValue(newConfig).await()
    }

    suspend fun deleteApp(packageName: String) {
        val sanitizedPackageName = packageName.replace(".", "_")
        database.child(sanitizedPackageName).removeValue().await()
    }

    suspend fun addLog(packageName: String, message: String) {
        val sanitizedPackageName = packageName.replace(".", "_")
        val logRef = database.child(sanitizedPackageName).child("logs")
        val newLog = LogEntry(message = message)

        val snapshot = logRef.get().await()
        val logs = snapshot.children.mapNotNull { it.getValue(LogEntry::class.java) }.toMutableList()
        logs.add(0, newLog)

        // Keep only the last 10 logs
        val updatedLogs = if (logs.size > 10) {
            logs.subList(0, 10)
        } else {
            logs
        }

        logRef.setValue(updatedLogs).await()
    }
}