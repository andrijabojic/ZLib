package com.example.zlib.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skeniraj knjigu") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("<-")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text("Ovde će uskoro biti kamera za skeniranje.")
        }
    }
}