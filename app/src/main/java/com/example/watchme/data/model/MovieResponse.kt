package com.example.watchme.data.model

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    val results: List<MovieDto>,
    @SerializedName("total_pages")
    val totalPages: Int
)
