package com.example.watchme.data.remote_db

import javax.inject.Inject

class MovieRemoteDataSource @Inject constructor(
    private val api: TmdbApi
) : BaseDataSource() {

    suspend fun fetchPopularMovies(lang: String = "en-US") = getResult {
        val response = api.getPopularMovies(language = lang)

        response
    }

    suspend fun fetchMovieDetails(movieId: Int, lang: String = "en-US") = getResult {
        api.getMovieDetails(movieId, language = lang)
    }

    suspend fun fetchGenres() = getResult { api.getGenres() }


    suspend fun searchMovies(query: String, lang: String) = getResult {
        api.searchMovies(query= query, language = lang)
    }

    suspend fun fetchTopRatedMovies(lang: String = "en-US") = getResult {
        api.getTopRatedMovies(language = lang)
    }

    suspend fun fetchUpcomingMovies(lang: String = "en-US") = getResult {
        api.getUpcomingMovies(language = lang)
    }
}