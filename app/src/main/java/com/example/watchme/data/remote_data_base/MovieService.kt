package com.example.watchme.data.remote_data_base

import com.example.watchme.data.model.genre.GenreResponse
import com.example.watchme.data.model.image.ImageResponse
import com.example.watchme.data.model.movie.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {

    @GET("movie/popular")
    suspend fun getPopularMovies(): Response<MovieResponse>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(): Response<MovieResponse>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String
    ): Response<MovieResponse>

    @GET("genre/movie/list")
    suspend fun getGenres(): Response<GenreResponse>

    @GET("movie/{movie_id}/images")
    suspend fun getMovieImages(
        @Path("movie_id") movieId: Int
    ): Response<ImageResponse>
}