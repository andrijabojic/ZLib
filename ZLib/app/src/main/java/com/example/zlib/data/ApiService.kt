package com.example.zlib.data

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.DELETE
import retrofit2.http.Body
import retrofit2.http.Path

interface ApiService {
    @GET("api/Books/Get-Books")
    suspend fun getBooks(): List<Book>

    @GET("api/Books/Get-Book/{id}")
    suspend fun getBook(@Path("id") id: Int): Book

    @POST("api/Books/Add-Book")
    suspend fun addBook(@Body book: BookCreateDto): Book

    @PATCH("api/Books/Update-Progress/{id}")
    suspend fun updateProgress(@Path("id") id: Int, @Body currentPage: Int): Unit

    @PATCH("api/Books/Update-Status/{id}")
    suspend fun updateStatus(@Path("id") id: Int, @Body newStatus: Int): Unit

    @DELETE("api/Books/Delete-Book/{id}")
    suspend fun deleteBook(@Path("id") id: Int): Unit
}