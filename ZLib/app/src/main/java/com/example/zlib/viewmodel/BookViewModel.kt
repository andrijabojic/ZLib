package com.example.zlib.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zlib.data.Book
import com.example.zlib.data.BookCreateDto
import com.example.zlib.data.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    fun fetchBooks() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getBooks()
                _books.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addBook(newBookDto: BookCreateDto) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.addBook(newBookDto)
                fetchBooks()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun deleteBook(id: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.deleteBook(id)
                fetchBooks()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateProgress(id: Int, currentPage: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.updateProgress(id, currentPage)
                fetchBooks()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}