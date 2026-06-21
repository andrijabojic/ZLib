package com.example.zlib.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zlib.viewmodel.BookViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSearchScreen(
    viewModel: BookViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onBookClick: (Int) -> Unit
) {
    val books by viewModel.filteredBooks.collectAsState()
    val query by viewModel.searchQuery.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchBooks()
    }

    Scaffold (topBar = {
        TopAppBar(
            title = { Text("Pretraga") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Nazad"
                    )
                }
            }
        )
    })
    { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Pretraži knjige...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Pretraga")
                },
                singleLine = true,
                shape = RoundedCornerShape(25.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (books.isEmpty()) {
                    item {
                        Text(
                            text = "Nema rezultata za: \"$query\"",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    items(books) { book ->
                        BookCard(book,onClick = { onBookClick(book.id) })
                    }
                }
            }
        }
    }
}