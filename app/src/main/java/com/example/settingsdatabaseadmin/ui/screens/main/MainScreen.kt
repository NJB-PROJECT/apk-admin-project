package com.example.settingsdatabaseadmin.ui.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.settingsdatabaseadmin.model.AppConfig
import com.example.settingsdatabaseadmin.model.ManagedApp
import com.example.settingsdatabaseadmin.ui.theme.SettingsDatabaseAdminTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onAppClick: (String) -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val apps by viewModel.apps.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newPackageName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Managed Apps") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add App")
            }
        }
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            items(apps) { app ->
                AppListItem(app = app, onClick = { onAppClick(app.packageName) })
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add New App") },
            text = {
                OutlinedTextField(
                    value = newPackageName,
                    onValueChange = { newPackageName = it },
                    label = { Text("Package Name") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addApp(newPackageName)
                        showDialog = false
                        newPackageName = ""
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AppListItem(app: ManagedApp, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = app.packageName, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val sampleApps = listOf(
        ManagedApp(packageName = "com.example.app1", config = AppConfig()),
        ManagedApp(packageName = "com.example.app2", config = AppConfig(isActive = false)),
        ManagedApp(packageName = "com.example.another.long.package.name", config = AppConfig())
    )

    SettingsDatabaseAdminTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Managed Apps") }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { }) {
                    Icon(Icons.Default.Add, contentDescription = "Add App")
                }
            }
        ) { paddingValues ->
            LazyColumn(contentPadding = paddingValues) {
                items(sampleApps) { app ->
                    AppListItem(app = app, onClick = {})
                }
            }
        }
    }
}