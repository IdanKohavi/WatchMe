package com.example.watchme.data.model

import com.google.gson.annotations.SerializedName

data class MovieDto(
    val id: Int,
    val title: String,
    @SerializedName("vote_average")
    val rating : Double,
    @SerializedName("poster_path")
    val posterPath : String,
    @SerializedName("overview")
    val description : String,
    @SerializedName("genre_ids")
    val genreIds : List<Int>,

    val images : ImageResponse
)
