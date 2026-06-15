package com.example.zlib.data
import com.google.gson.annotations.SerializedName

data class Book(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("pageCount") val pageCount: Int,
    @SerializedName("currentPage") val currentPage: Int = 0,
    @SerializedName("status") val statusInt: Int = 1,
    @SerializedName("description") val description: String? = null
){// Ovo je property koji ćeš koristiti u UI-ju
    val status: BookStatus
        get() = BookStatus.fromInt(statusInt)
}