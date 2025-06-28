package com.example.watchme.utils

import androidx.room.TypeConverter


class Converters {
    private val separator = ","

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString(separator)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split(separator)?.filter { it.isNotBlank() } ?: emptyList()
    }
}