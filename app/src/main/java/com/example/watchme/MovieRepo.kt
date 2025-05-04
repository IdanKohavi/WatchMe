package com.example.watchme

import android.app.Application
import com.example.watchme.data.model.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MovieRepo(application: Application) {

    private var movieDao:MovieDao?
    init{

        val db = MovieDB.getDB(application.applicationContext)
        movieDao = db.movieDao()

    }

    fun getAllMovies() = movieDao?.getAllMovies()

    suspend fun addMovie(movie:Movie) = movieDao?.addMovie(movie)

    suspend fun addMovies(movies: List<Movie>) = movieDao?.addMovies(movies)

    suspend fun deleteMovie(movie:Movie) = movieDao?.deleteMovie(movie)

    suspend fun updateMovie(movie:Movie) = movieDao?.updateMovie(movie)

    fun getMovieById(id:Int) = movieDao?.getMovieById(id)

}