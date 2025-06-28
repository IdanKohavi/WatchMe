package com.example.watchme.data.model.movie

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.watchme.utils.Converters
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey
    val id: Int,
    val title: String,

    @SerializedName("vote_average")
    val rating: Double,

    @SerializedName("poster_path")
    val posterUrl: String,

    @SerializedName("overview")
    val description: String,

    @SerializedName("genre_ids")
    @TypeConverters(Converters::class)
    val genres: List<String>,

    @TypeConverters(Converters::class)
    val images: List<String>?,

    var isFavorite: Boolean = false
)