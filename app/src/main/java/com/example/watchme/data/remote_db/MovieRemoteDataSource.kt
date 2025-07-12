package com.example.watchme.data.remote_db

import android.util.Log
import il.co.syntax.utils.data.remote_db.BaseDataSource
import javax.inject.Inject

class MovieRemoteDataSource @Inject constructor(
    private val api: TmdbApi
) : BaseDataSource() {

    suspend fun fetchPopularMovies() = getResult {
        val response = api.getPopularMovies()
        Log.d("MovieRemoteDataSource", "API Called, success = ${response.isSuccessful}, code = ${response.code()}, message = ${response.message()}")
        response
    }

    suspend fun fetchMovieDetails(movieId: Int) = getResult { api.getMovieDetails(movieId) }

    suspend fun fetchGenres() = getResult { api.getGenres() }

}