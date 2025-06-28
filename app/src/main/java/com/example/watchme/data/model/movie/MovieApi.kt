package com.example.watchme.data.model.movie

import com.google.gson.annotations.SerializedName

data class MovieApi(
    val id: Int,
    val title: String,
    @SerializedName("vote_average") val rating: Double,
    @SerializedName("poster_path") val posterUrl: String?,
    val overview: String,
    @SerializedName("genre_ids") val genreIds: List<Int>
)
