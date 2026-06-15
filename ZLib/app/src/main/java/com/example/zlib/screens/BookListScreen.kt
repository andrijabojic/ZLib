package com.example.zlib.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zlib.viewmodel.BookViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.zlib.data.Book
@Composable
fun BookListScreen(viewModel: BookViewModel = viewModel(),
                   onNavigateToAdd: () -> Unit) {
    val books by viewModel.books.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchBooks()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {onNavigateToAdd()},
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(
                text = "ZLib",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(books) { book ->
                    BookCard(book)
                }
            }
        }
    }
}

@Composable
fun BookCard(book: Book) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = book.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = "Autor: ${book.author}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Strana: ${book.currentPage} / ${book.pageCount}", style = MaterialTheme.typography.bodySmall)
        }
    }
}