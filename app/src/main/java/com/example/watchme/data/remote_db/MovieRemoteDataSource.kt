package com.example.watchme.data.remote_db

import android.util.Log
import il.co.syntax.utils.data.remote_db.BaseDataSource
import javax.inject.Inject

class MovieRemoteDataSource @Inject constructor(
    private val api: TmdbApi
) : BaseDataSource() {

    suspend fun fetchPopularMovies(lang: String = "en-US") = getResult {
        val response = api.getPopularMovies(language = lang)
        Log.d("MovieRemoteDataSource", "API Called with language=$lang, success = ${response.isSuccessful}, code = ${response.code()}, message = ${response.message()}")
        response
    }

    suspend fun fetchMovieDetails(movieId: Int, lang: String = "en-US") = getResult {
        api.getMovieDetails(movieId, language = lang)
    }

    suspend fun fetchGenres() = getResult { api.getGenres() }

}