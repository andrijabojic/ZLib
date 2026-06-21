package com.example.zlib

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zlib.screens.BookListScreen
import com.example.zlib.screens.AddBookScreen
import com.example.zlib.screens.BookDetailScreen
import com.example.zlib.screens.BookSearchScreen
import com.example.zlib.screens.IsbnScannerScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "bookList") {

        composable("bookList") {
            BookListScreen(
                onNavigateToAdd = { navController.navigate("addBook") },
                onNavigateToSearch = { navController.navigate("search") },
                onBookClick = { bookId ->
                    navController.navigate("bookDetail/$bookId")
                }
            )
        }

        composable("addBook") {
            AddBookScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToScanner = { navController.navigate("scanner") }

            )
        }
        composable("scanner") {
            IsbnScannerScreen(
                onBarcodeScanned = { isbn ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("scanned_isbn", isbn)
                    navController.popBackStack("addBook", false)
                }
            )
        }
        composable("search") {
            BookSearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onBookClick = { bookId ->
                    navController.navigate("bookDetail/$bookId")
                }
            )
        }
        composable("bookDetail/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")
            BookDetailScreen(
                bookId = bookId,
                onNavigateBack = { navController.popBackStack() },
                onEditBook = { id:String -> navController.navigate("editBook/$id") },
            )
        }
    }
}