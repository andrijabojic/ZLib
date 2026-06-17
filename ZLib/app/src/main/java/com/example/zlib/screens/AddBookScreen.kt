package com.example.zlib.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zlib.data.Book
import com.example.zlib.data.BookCreateDto
import com.example.zlib.viewmodel.BookViewModel
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(viewModel: BookViewModel = viewModel(), onNavigateBack: () -> Unit,onCancel: () -> Unit) {
    var isbnInput by remember { mutableStateOf("") }
    val searchState by viewModel.searchState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dodaj knjigu") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Nazad")
                    }
                }
            )
        }
    ) { paddingValues ->
    Column(modifier = Modifier.padding(paddingValues).padding(16.dp).verticalScroll(rememberScrollState())) {

        OutlinedTextField(
            value = isbnInput,
            onValueChange = { isbnInput = it },
            label = { Text("Unesite ISBN") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Row {
                    IconButton(onClick = { /* TODO: Implementiraj otvaranje kamere */ }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Skeniraj ISBN")
                    }
                    IconButton(onClick = { viewModel.searchBook(isbnInput) }) {
                        Icon(Icons.Default.Search, contentDescription = "Pretraži")
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = searchState) {
            is BookViewModel.BookSearchState.Found -> {
                BookForm(
                    initialBook = state.book,
                    onAdded = { viewModel.resetSearchState() },
                    onCancel = { viewModel.resetSearchState() }
                )
            }
            is BookViewModel.BookSearchState.NotFound -> {
                BookForm(
                    initialIsbn = state.isbn,
                    onAdded = { viewModel.resetSearchState() },
                    onCancel = { viewModel.resetSearchState() }
                )
            }
            is BookViewModel.BookSearchState.Error -> {
                Text("Greška: ${state.message}", color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
        }
    }
}

@Composable
fun BookForm(
    initialBook: Book? = null,
    initialIsbn: String? = null,
    viewModel: BookViewModel = viewModel(),
    onAdded: () -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(initialBook?.title ?: "") }
    var author by remember { mutableStateOf(initialBook?.author ?: "") }
    var isbn by remember { mutableStateOf(initialBook?.isbn ?: initialIsbn ?: "") }
    var pageCount by remember { mutableStateOf(initialBook?.pageCount?.toString() ?: "") }

    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text("Potvrdi ili izmeni podatke:", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Naslov") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Autor") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = isbn, onValueChange = { isbn = it }, label = { Text("ISBN") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = pageCount, onValueChange = { pageCount = it }, label = { Text("Broj stranica") }, modifier = Modifier.fillMaxWidth())

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Odustani")
            }

            Button(
                onClick = {
                    val newBook = BookCreateDto(
                        title = title,
                        author = author,
                        isbn = isbn,
                        pageCount = pageCount.toIntOrNull() ?: 0,
                        description = "",
                        imagePath = ""
                    )
                    viewModel.addBook(newBook)
                    onAdded()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Sačuvaj")
            }
        }
    }
}