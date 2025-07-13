package com.example.watchme.data.mappers

import com.example.watchme.data.model.MovieDto
import com.example.watchme.data.model.Movie
import com.example.watchme.utils.Constants

fun MovieDto.toMovie(): Movie {

    val imageUrls = images?.backdrops?.map {
        "${Constants.IMAGE_BASE_URL}${it.filePath}"
    } ?: emptyList()

    val genresNames = genres?.map {it.name} ?: GenreMapper.mapIdsToGenres(genreIds) //if the genresNames exist - map it to name, else - use the genre mapper.

    return Movie(
        id = id,
        title = title,
        rating = rating,
        posterUrl = "https://image.tmdb.org/t/p/w500$posterPath",
        description = description,
        genres = genresNames,
        images = imageUrls
    )
}

