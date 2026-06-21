package com.example.zlib.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.zlib.BuildConfig
import com.example.zlib.R
import com.example.zlib.data.BookStatus
import com.example.zlib.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: String?,
    viewModel: BookViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onEditBook: (String) -> Unit
) {
    val books by viewModel.books.collectAsState()
    LaunchedEffect(Unit) {
        if (books.isEmpty()) {
            viewModel.fetchBooks()
        }
    }
    val book = books.find { it.id.toString() == bookId }
    android.util.Log.d("DEBUG_KNJIGE", "Broj knjiga: ${books.size}, Traženi ID: $bookId")

    Scaffold(

        topBar = {
            TopAppBar(
                title = { Text("Detalji knjige") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } },
                actions = {
                    IconButton(onClick = { book?.let { onEditBook(it.id.toString()) } }) {
                        Icon(Icons.Default.Edit, contentDescription = "Izmeni")
                    }
                }
            )
        }
    ) { padding ->
        book?.let { currentBook ->
            android.util.Log.d("DEBUG_KNJIGA", "Puna knjiga: $currentBook")
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val baseUrl = "http://${BuildConfig.SERVER_IP}:5000/uploads/covers/"
                    val fullImageUrl = book.imagePath?.let { baseUrl + it }
                    AsyncImage(
                        model = fullImageUrl,
                        contentDescription = "Korice",
                        modifier = Modifier
                            .width(120.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = currentBook.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Autor: ${currentBook.author}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoSection("Status", currentBook.status.name)

                    InfoSection("Napredak", "${currentBook.currentPage} / ${currentBook.pageCount} stranica")
                    val displayDescription = if (currentBook.description.isNullOrBlank()) {
                        "Nema opisa."
                    } else {
                        currentBook.description
                    }

                    Text(
                        text = displayDescription,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (currentBook.status == BookStatus.Unread) {
                    Button(
                        onClick = { viewModel.updateStatus(currentBook.id, BookStatus.Reading) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Počni sa čitanjem")
                    }
                }
                else if(currentBook.status == BookStatus.Finished)
                {
                    Button(
                    onClick = { viewModel.updateStatus(currentBook.id, BookStatus.Reading) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ponovo počni sa čitanjem")
                }

                }
            }
        }
    }
}
@Composable
fun InfoSection(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}