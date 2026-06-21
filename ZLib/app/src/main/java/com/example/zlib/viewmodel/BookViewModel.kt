package com.example.zlib.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zlib.data.Book
import com.example.zlib.data.BookCreateDto
import com.example.zlib.data.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody

class BookViewModel : ViewModel() {

    private val apiService = RetrofitClient.instance
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    private val _tempImageUri = MutableStateFlow<Uri?>(null)
    val tempImageUri: StateFlow<Uri?> = _tempImageUri

    fun setTempImageUri(uri: Uri?) {
        _tempImageUri.value = uri
    }
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
    fun addBookWithCover(context: Context, newBookDto: BookCreateDto) {
        viewModelScope.launch {
            try {
                val createdBook = RetrofitClient.instance.addBook(newBookDto)

                _tempImageUri.value?.let { uri ->
                    uploadBookCover(context, createdBook.id, uri)
                    _tempImageUri.value = null
                }

                fetchBooks()
            } catch (e: Exception) {
                Log.e("MOJ_LOG", "Greska pri dodavanju: ${e.message}")
            }
        }
    }
    fun uploadBookCover(context: Context, bookId: Int, uri: Uri) {
        viewModelScope.launch {
            Log.d("UPLOAD_DEBUG", "Pokrećem upload za ID: $bookId")
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    Log.e("UPLOAD_DEBUG", "InputStream je null! URI nije validan.")
                    return@launch
                }

                val file = File(context.cacheDir, "cover_${bookId}.jpg")
                inputStream.use { input -> file.outputStream().use { output -> input.copyTo(output) } }
                Log.d("UPLOAD_DEBUG", "Fajl kreiran: ${file.absolutePath}, veličina: ${file.length()}")

                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response = apiService.uploadCover(bookId, body)

                if (response.isSuccessful) {
                    Log.d("UPLOAD_DEBUG", "Uspešno uploadovano na server!")
                } else {
                    Log.e("UPLOAD_DEBUG", "Server vratio grešku: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("UPLOAD_DEBUG", "Exception u upload-u: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val filteredBooks = combine(books, searchQuery) { bookList, query ->
        if (query.isBlank()) bookList
        else bookList.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.author.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }
}