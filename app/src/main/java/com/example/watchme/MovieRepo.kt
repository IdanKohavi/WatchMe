package com.example.watchme

import android.app.Application
import com.example.watchme.data.model.Movie

class MovieRepo(application: Application){

    private var movieDao:MovieDao?
    init{

        val db = MovieDB.getDB(application.applicationContext)
        movieDao = db.movieDao()

    }

    fun getAllMovies() = movieDao?.getAllMovies()

    fun addMovie(movie:Movie) = movieDao?.addMovie(movie)

    fun addMovies(movies: List<Movie>) = movieDao?.addMovies(movies)

    fun deleteMovie(movie:Movie) = movieDao?.deleteMovie(movie)

    fun updateMovie(movie:Movie) = movieDao?.updateMovie(movie)

    fun getMovieById(id:Int) = movieDao?.getMovieById(id)

}