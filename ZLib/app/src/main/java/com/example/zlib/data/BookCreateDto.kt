package com.example.zlib.data

data class BookCreateDto(
    val title: String,
    val author: String,
    val isbn: String?,
    val description: String?,
    val pageCount: Int,
    val imagePath: String?
)