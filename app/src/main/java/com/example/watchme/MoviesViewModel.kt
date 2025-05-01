package com.example.watchme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class MoviesViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = MovieRepo(application)

    val movies : LiveData<List<Movie>>? = repo.getMovies()

    fun addMovie(movie: Movie) {
        repo.addMovie(movie)
    }

    fun deleteMovie(movie: Movie) {
        repo.deleteMovie(movie)
    }
    fun updateMovie(movie: Movie) {
        repo.updateMovie(movie)
    }


}