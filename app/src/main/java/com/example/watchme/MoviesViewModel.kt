package com.example.watchme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.watchme.data.model.Movie

class MoviesViewModel(application: Application) : AndroidViewModel(application) {

    private val _movies: MutableLiveData<List<Movie>?> = MutableLiveData(null)
    private val _movieCount = MutableLiveData<Int?>(0)
    private val _movie = MutableLiveData<Movie?>(null)

    val movieCount: LiveData<Int?> get() = _movieCount
    val movie: LiveData<Movie?> get() = _movie
    val movies: LiveData<List<Movie>?> get() = _movies

    fun updateMovieCount(count: Int?) {
        if (count == null) {
            _movieCount.value = 0
            return
        }
        _movieCount.value = count
    }

    fun assignMovie(movie: Movie?) {
        _movie.value = movie
    }

    fun assignMovies(movies: List<Movie>?) {
        _movies.value = movies?.toList()
        updateMovieCount(movies?.size)
    }

    fun deleteMovie(movie: Movie) {
        val updatedMovies = _movies.value?.toMutableList()?.apply {
            remove(movie)
        }
        _movies.value = updatedMovies
        updateMovieCount(updatedMovies?.size ?: 0)
    }
}