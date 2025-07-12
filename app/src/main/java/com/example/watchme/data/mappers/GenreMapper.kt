package com.example.watchme.data.mappers

import com.example.watchme.data.model.Genre

object GenreMapper {
    private var genreMap: Map<Int, String> = emptyMap()

    fun setGenreMap(genres: List<Genre>) {
        genreMap = genres.associate { it.id to it.name }
    }

    fun mapIdsToGenres(ids: List<Int>?): List<String> {
        return ids?.mapNotNull { genreMap[it] } ?: emptyList()
    }
}