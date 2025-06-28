package com.example.watchme.data.remote_data_base

import javax.inject.Inject

class MovieRemoteDataSource @Inject constructor(
    private val movieService: MovieService
) : BaseDataSource() {

    suspend fun getPopularMovies() = getResult { movieService.getPopularMovies() }
    suspend fun getTopRatedMovies() = getResult { movieService.getTopRatedMovies() }
    suspend fun searchMovies(query: String) = getResult { movieService.searchMovies(query) }
    suspend fun getGenres() = getResult { movieService.getGenres() }
    suspend fun getMovieImages(movieId: Int) = getResult { movieService.getMovieImages(movieId) }

}