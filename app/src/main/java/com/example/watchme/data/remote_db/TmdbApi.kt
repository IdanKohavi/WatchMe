package com.example.watchme.data.remote_db

import com.example.watchme.data.model.GenreResponse
import com.example.watchme.data.model.MovieDto
import com.example.watchme.data.model.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("movie/popular")
    suspend fun getPopularMovies(): Response<MovieResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("append_to_response") appendToResponse: String = "images"
    ) : Response<MovieDto>

    @GET("genre/movie/list")
    suspend fun getGenres(): Response<GenreResponse>


}