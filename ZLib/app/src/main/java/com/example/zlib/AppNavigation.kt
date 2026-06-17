package com.example.zlib

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zlib.screens.BookListScreen
import com.example.zlib.screens.AddBookScreen // Ovo ćemo napraviti sledeće

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "bookList") {

        composable("bookList") {
            BookListScreen(
                onNavigateToAdd = { navController.navigate("addBook") }
            )
        }

        composable("addBook") {
            AddBookScreen(
                onNavigateBack = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }

            )
        }
    }
}