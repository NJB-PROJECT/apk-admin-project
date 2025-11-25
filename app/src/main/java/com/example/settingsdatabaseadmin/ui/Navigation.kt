package com.example.settingsdatabaseadmin.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.settingsdatabaseadmin.ui.screens.detail.DetailScreen
import com.example.settingsdatabaseadmin.ui.screens.main.MainScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(onAppClick = { packageName ->
                navController.navigate("detail/$packageName")
            })
        }
        composable(
            "detail/{packageName}",
            arguments = listOf(navArgument("packageName") { type = NavType.StringType })
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            DetailScreen(packageName = packageName, onNavigateUp = { navController.popBackStack() })
        }
    }
}