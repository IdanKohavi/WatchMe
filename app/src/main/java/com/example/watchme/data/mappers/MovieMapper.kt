package com.example.watchme.data.mappers

import com.example.watchme.data.model.MovieDto
import com.example.watchme.data.model.Movie
import com.example.watchme.utils.Constants

fun MovieDto.toMovie(): Movie {

    val imageUrls = images?.backdrops?.map {
        "${Constants.IMAGE_BASE_URL}${it.filePath}"
    } ?: emptyList()

    return Movie(
        id = id,
        title = title,
        rating = rating,
        posterUrl = "https://image.tmdb.org/t/p/w500$posterPath",
        description = description,
        genres = GenreMapper.mapIdsToGenres(genreIds),
        images = imageUrls
    )
}

