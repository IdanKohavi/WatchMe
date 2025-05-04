package com.example.watchme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.watchme.data.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoviesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepo = MovieRepo(application)
    private val _movie = MutableLiveData<Movie?>(null)

    val allMovies: LiveData<List<Movie>>? = repository.getAllMovies()
    val movie: LiveData<Movie?> get() = _movie


    fun assignMovie(movie: Movie?) {
        _movie.value = movie
    }

    //For the based movies ( 3 movies )
    fun assignMovies(movies: List<Movie>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMovies(movies)
        }
    }

    fun addMovie(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO){
            repository.addMovie(movie)
        }
    }

    fun deleteMovie(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMovie(movie)
        }
    }

    fun updateMovie(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMovie(movie)
        }
    }

    fun getMovieById(id: Int): LiveData<Movie>? {
        return repository.getMovieById(id)
    }

}