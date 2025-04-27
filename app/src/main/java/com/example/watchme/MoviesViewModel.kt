package com.example.watchme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.watchme.data.model.Movie

class MoviesViewModel(application: Application) : AndroidViewModel(application) {

    private val _movieCount = MutableLiveData<Int>()
    private val _movie = MutableLiveData<Movie>()

    val movieCount: LiveData<Int> get() = _movieCount
    val movie: LiveData<Movie> get() = _movie

    fun updateMovieCount(count: Int) {
        _movieCount.value = count
    }

    fun assignMovie(movie: Movie) {
        _movie.value = movie
    }
}