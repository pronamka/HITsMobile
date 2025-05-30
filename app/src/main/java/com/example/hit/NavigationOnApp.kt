package com.example.hit

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

val showDialog = mutableStateOf(false)
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Destinations.START_SCREEN
    ) {
        composable(Destinations.START_SCREEN) {
            StartScr(
                navController
            )
        }
        composable(Destinations.CODE_SCREEN) {
            CodeScreen(navController)
        }
        composable(Destinations.DOCUMENTATION_SCREEN) {
            DocumentationScreen(navController)
        }
    }
}