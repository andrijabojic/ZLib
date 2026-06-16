package com.example.zlib.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun BookListScreen(
    viewModel: BookViewModel = viewModel(),
    onNavigateToAdd: () -> Unit
) {
    val books by viewModel.books.collectAsState()

    val readingBooks = remember(books) { books.filter { it.status == BookStatus.Reading } }

    LaunchedEffect(Unit) {
        viewModel.fetchBooks()
    }

    Scaffold(
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
                    text = "ZLib",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
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
                        BookCarouselItem(book, isLarge = true)
                    }
                }
            }

            item {
                Text(
                    text = "Sve knjige",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )
            }

            items(books) { book ->
                BookCard(book)
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
@Composable
fun BookCarousel(books: List<Book>) {
    LazyRow(
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books) { book ->
            BookCarouselItem(book)
        }
    }
}
@Composable
fun BookCarouselItem(book: Book, isLarge: Boolean = false) {
    Card(
        modifier = Modifier
            .width(if (isLarge) 200.dp else 160.dp)
            .height(if (isLarge) 300.dp else 240.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(contentAlignment = Alignment.BottomStart) {
            Image(
                painter = painterResource(id = R.drawable.ic_book_placeholder),
                contentDescription = "Slika knjige",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
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