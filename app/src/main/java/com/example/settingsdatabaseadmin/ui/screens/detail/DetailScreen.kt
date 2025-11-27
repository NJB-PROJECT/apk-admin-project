package com.example.settingsdatabaseadmin.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.settingsdatabaseadmin.model.AppConfig
import com.example.settingsdatabaseadmin.model.LogEntry
import com.example.settingsdatabaseadmin.ui.theme.SettingsDatabaseAdminTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    packageName: String,
    onNavigateUp: () -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    val app by viewModel.app.collectAsState()

    LaunchedEffect(packageName) {
        viewModel.loadApp(packageName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(packageName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        app?.let { managedApp ->
            DetailContent(
                managedApp = managedApp,
                onConfigChange = { newConfig, logMessage ->
                    viewModel.updateConfig(packageName, newConfig, logMessage)
                },
                onDelete = {
                    viewModel.deleteApp(packageName)
                    onNavigateUp()
                },
                modifier = Modifier.padding(paddingValues)
            )
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    val sampleLogs = listOf(
        LogEntry(message = "Configuration updated."),
        LogEntry(message = "Maintenance mode enabled."),
        LogEntry(message = "Application created.")
    )
    val sampleApp = com.example.settingsdatabaseadmin.model.ManagedApp(
        packageName = "com.example.app1",
        config = AppConfig(latestVersionName = "1.2.3", latestVersionCode = 123),
        logs = sampleLogs
    )

    SettingsDatabaseAdminTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(sampleApp.packageName) },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            DetailContent(
                managedApp = sampleApp,
                onConfigChange = { _, _ -> },
                onDelete = {},
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun DetailContent(
    managedApp: com.example.settingsdatabaseadmin.model.ManagedApp,
    onConfigChange: (AppConfig, String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var latestVersionCode by remember { mutableStateOf(managedApp.config.latestVersionCode.toString()) }
    var latestVersionName by remember { mutableStateOf(managedApp.config.latestVersionName) }
    var maintenanceCode by remember { mutableStateOf(managedApp.config.maintenanceCode) }
    var isActive by remember { mutableStateOf(managedApp.config.isActive) }
    var maintenanceMode by remember { mutableStateOf(managedApp.config.maintenanceMode) }

    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ConfigEditor(
                latestVersionCode = latestVersionCode,
                onLatestVersionCodeChange = { latestVersionCode = it },
                latestVersionName = latestVersionName,
                onLatestVersionNameChange = { latestVersionName = it },
                maintenanceCode = maintenanceCode,
                onMaintenanceCodeChange = { maintenanceCode = it },
                isActive = isActive,
                onIsActiveChange = { isActive = it },
                maintenanceMode = maintenanceMode,
                onMaintenanceModeChange = { maintenanceMode = it },
                onSave = {
                    val newConfig = managedApp.config.copy(
                        latestVersionCode = latestVersionCode.toIntOrNull() ?: 0,
                        latestVersionName = latestVersionName,
                        maintenanceCode = maintenanceCode,
                        isActive = isActive,
                        maintenanceMode = maintenanceMode
                    )
                    onConfigChange(newConfig, "Configuration updated.")
                },
                onDelete = onDelete
            )
        }
        item {
            Text("Logs", style = MaterialTheme.typography.titleMedium)
        }
        items(managedApp.logs) { log ->
            LogItem(log = log)
        }
    }
}

@Composable
fun ConfigEditor(
    latestVersionCode: String,
    onLatestVersionCodeChange: (String) -> Unit,
    latestVersionName: String,
    onLatestVersionNameChange: (String) -> Unit,
    maintenanceCode: String,
    onMaintenanceCodeChange: (String) -> Unit,
    isActive: Boolean,
    onIsActiveChange: (Boolean) -> Unit,
    maintenanceMode: Boolean,
    onMaintenanceModeChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = latestVersionCode,
            onValueChange = onLatestVersionCodeChange,
            label = { Text("Latest Version Code") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = latestVersionName,
            onValueChange = onLatestVersionNameChange,
            label = { Text("Latest Version Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = maintenanceCode,
            onValueChange = onMaintenanceCodeChange,
            label = { Text("Maintenance Code") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Is Active")
            Spacer(Modifier.weight(1f))
            Switch(checked = isActive, onCheckedChange = onIsActiveChange)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Maintenance Mode")
            Spacer(Modifier.weight(1f))
            Switch(checked = maintenanceMode, onCheckedChange = onMaintenanceModeChange)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onSave) {
                Text("Save Changes")
            }
            Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Delete App")
            }
        }
    }
}

@Composable
fun LogItem(log: LogEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = log.message, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = java.text.SimpleDateFormat.getDateTimeInstance().format(java.util.Date(log.timestamp)),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}