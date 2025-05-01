package com.example.watchme

import android.app.Application

class MovieRepo(application: Application){

    private var movieDao:MovieDao?
    init{

        val db = MovieDB.getDB(application.applicationContext)
        movieDao = db?.movieDao()

    }

    fun getMovies() = movieDao?.getMovies()

    fun addMovie(movie:Movie) = movieDao?.addMovie(movie)

    fun deleteMovie(movie:Movie) = movieDao?.deleteMovie(movie)

    fun updateMovie(movie:Movie) = movieDao?.updateMovie(movie)

    fun  getMovieByTitle(title:String) = movieDao?.getMovieByTitle(title)

}