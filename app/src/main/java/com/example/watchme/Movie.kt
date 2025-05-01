package com.example.watchme

import androidx.room.*

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val rating: Double,
    val posterURL: String,
    val description: String,
    val genres: List<String>,
    val images: List<String>?,
    var isFavorite: Boolean = false
)
