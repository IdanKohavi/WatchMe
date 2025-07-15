package com.example.watchme.data.mappers

import android.util.Log
import com.example.watchme.data.model.Genre

object GenreMapper {
    private var genreMap: Map<Int, String> = emptyMap()

    fun setGenreMap(genres: List<Genre>) {
        genreMap = genres.associate { it.id to it.name }
        Log.d("GenreMapper", "Genre map set: $genreMap")
    }

    fun mapIdsToGenres(ids: List<Int>?): List<String> {
        val genres =  ids?.mapNotNull { genreMap[it] } ?: emptyList()
        Log.d("GenreMapper", "Mapping IDs: $ids -> Genres: $genres")
        return genres
    }
}