package com.example.zlib.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zlib.data.Book
import com.example.zlib.viewmodel.BookViewModel
import com.example.zlib.R
import com.example.zlib.data.BookStatus
import coil.compose.AsyncImage
import com.example.zlib.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    viewModel: BookViewModel = viewModel(),
    onNavigateToAdd: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onBookClick: (Int) -> Unit
) {
    val books by viewModel.books.collectAsState()

    val recommendations = remember(books) {
        books.shuffled()
            .filter { it.status != BookStatus.Reading && it.status != BookStatus.Finished }
            .take(3)
    }

    val readingBooks = remember(books) { books.filter { it.status == BookStatus.Reading } }

    LaunchedEffect(Unit) {
        viewModel.fetchBooks()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ZLib") },
                actions = {
                    IconButton(onClick = { onNavigateToSearch() }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Pretraži knjige"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAdd() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", color = Color.White)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                Text(
                    text = "Trenutno citate",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(readingBooks) { book ->
                        BookCarouselItem(book, isLarge = true, viewModel)
                    }
                }
            }

            item {
                Text(
                    text = "Preporuka za danas",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }

            items(recommendations) { book ->
                BookCard(book,onClick = { onBookClick(book.id) })
            }
        }
    }
}

@Composable
fun BookCard(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = book.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = book.author, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
@Composable
fun BookCarousel(books: List<Book>, viewModel: BookViewModel) {
    LazyRow(
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books) { book ->
            BookCarouselItem(book = book, viewModel = viewModel)
        }
    }
}
@Composable
fun BookCarouselItem(book: Book,
                     isLarge: Boolean = false,
                     viewModel: BookViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    val books by viewModel.books.collectAsState()

    val currentBook = books.find { it.id == book.id } ?: book

    if (showDialog) {
        UpdatePageDialog(
            book = currentBook,
            onDismiss = { showDialog = false },
            onConfirm = { newPage ->
                viewModel.updateProgress(book.id, newPage)
                showDialog = false
            }
        )
    }
    Card(
        modifier = Modifier
            .width(if (isLarge) 200.dp else 160.dp)
            .height(if (isLarge) 300.dp else 240.dp)
            .padding(8.dp)
            .clickable { showDialog = true },
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(contentAlignment = Alignment.BottomStart) {
            val baseUrl = "http://${BuildConfig.SERVER_IP}:5000/uploads/covers/"
            val fullImageUrl = book.imagePath?.let { baseUrl + it }

            AsyncImage(
                model = fullImageUrl,
                contentDescription = "Slika knjige",
                placeholder = painterResource(id = R.drawable.ic_book_placeholder),
                error = painterResource(id = R.drawable.ic_book_placeholder),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = "${book.currentPage}/${book.pageCount}",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                text = book.title,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
@Composable
fun UpdatePageDialog(
    book: Book,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var textValue by remember(book.currentPage) {
        mutableStateOf(book.currentPage.toString())
    }
    LaunchedEffect(book.currentPage) {
        textValue = book.currentPage.toString()
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ažuriraj napredak") },
        text = {
            Column {
                Text("Knjiga: ${book.title}")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { if (it.all { char -> char.isDigit() }) textValue = it },
                    label = { Text("Trenutna stranica") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(textValue.toIntOrNull() ?: 0) }) {
                Text("Sačuvaj")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Odustani")
            }
        }
    )
}