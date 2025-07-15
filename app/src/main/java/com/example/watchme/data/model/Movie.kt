package com.example.watchme.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.watchme.utils.Converters

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey
    val id: Int = 0,
    val title: String,
    val rating: Double,
    val posterUrl: String,
    val description: String,

    @TypeConverters(Converters::class)
    val genres: List<String>,

    @TypeConverters(Converters::class)
    val images: List<String>?,

    var isFavorite: Boolean = false,

    @TypeConverters(Converters::class)
    val types: List<String> = listOf("popular")
) {
    @Ignore
    var genresId : List<Int> = emptyList()
}