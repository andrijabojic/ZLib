package com.example.zlib.data

enum class BookStatus(val value: Int) {
    Unread(1),
    Reading(2),
    Finished(3);

    companion object {
        fun fromInt(value: Int) = values().firstOrNull { it.value == value } ?: Unread
    }
}