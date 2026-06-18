package com.example.zlib.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zlib.data.Book
import com.example.zlib.data.BookCreateDto
import com.example.zlib.data.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class BookViewModel : ViewModel() {

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    fun fetchBooks() {
        viewModelScope.launch {
            try {
                Log.d("MOJ_LOG", "Pokušavam dohvatanje knjiga...")
                val response = RetrofitClient.instance.getBooks()
                Log.d("MOJ_LOG", "Uspelo! Dobio sam ${response.size} knjiga.")
                _books.value = response
            } catch (e: Exception) {
                Log.e("MOJ_LOG", "GRESKA PRI POZIVU: ${e.message}", e)
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
    sealed class BookSearchState {
        object Idle : BookSearchState()
        data class Found(val book: Book) : BookSearchState()
        data class NotFound(val isbn: String) : BookSearchState()
        data class Error(val message: String) : BookSearchState()
    }
    private val _searchState = MutableStateFlow<BookSearchState>(BookSearchState.Idle)
    val searchState: StateFlow<BookSearchState> = _searchState

    fun searchBook(isbn: String) {
        viewModelScope.launch {
            try {
                val book = RetrofitClient.instance.getBookByIsbn(isbn)
                _searchState.value = BookSearchState.Found(book)
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    _searchState.value = BookSearchState.NotFound(isbn)
                } else {
                    _searchState.value = BookSearchState.Error("Greška na serveru")
                }
            }
        }
    }
    fun resetSearchState() {
        _searchState.value = BookSearchState.Idle
    }
}