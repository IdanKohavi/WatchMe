package com.example.watchme.data.model

data class Movie(
    val id: Int = 0,
    val title: String,
    val rating: Double,
    val posterUrl: String,
    val description: String,
    val genres: List<String>,
    val images: List<String>?,
    val posterResId: Int?
)