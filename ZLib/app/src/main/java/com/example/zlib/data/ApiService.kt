package com.example.zlib.data

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.DELETE
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Path
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("api/Books/Get-Books")
    suspend fun getBooks(): List<Book>

    @GET("api/Books/Get-Book/{id}")
    suspend fun getBook(@Path("id") id: Int): Book

    @POST("api/Books/Add-Book")
    suspend fun addBook(@Body book: BookCreateDto): Book

    @PATCH("api/Books/Update-Progress/{id}")
    suspend fun updateProgress(@Path("id") id: Int, @Body currentPage: Int): retrofit2.Response<Unit>

    @PATCH("api/Books/Update-Status/{id}")
    suspend fun updateStatus(@Path("id") id: Int, @Body newStatus: Int): Response<Unit>

    @DELETE("api/Books/Delete-Book/{id}")
    suspend fun deleteBook(@Path("id") id: Int): Unit

    @GET("api/Books/Search-By-Isbn/{isbn}")
    suspend fun getBookByIsbn(@Path("isbn") isbn: String): Book

    @Multipart
    @POST("api/Books/Upload-Cover/{id}")
    suspend fun uploadCover(
        @Path("id") bookId: Int,
        @Part file: MultipartBody.Part
    ): Response<Map<String, String>>
}