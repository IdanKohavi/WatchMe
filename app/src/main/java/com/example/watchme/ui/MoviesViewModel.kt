package com.example.watchme.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchme.data.repository.MovieRepo
import com.example.watchme.data.model.movie.Movie
import com.example.watchme.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: MovieRepo
) : ViewModel() {

    val allMovies: LiveData<List<Movie>> = repository.getAllMovies()

    private val _movie = MutableLiveData<Movie?>()
    val movie: LiveData<Movie?> get() = _movie

    private val _popularMovies = repository.fetchPopularMovies()
    val popularMovies: LiveData<Resource<List<Movie>>> get() = _popularMovies

    private val _topRatedMovies = repository.fetchTopRatedMovies()
    val topRatedMovies: LiveData<Resource<List<Movie>>> get() = _topRatedMovies

    private val _searchedMovies = MutableLiveData<Resource<List<Movie>>>()
    val searchedMovies: LiveData<Resource<List<Movie>>> get() = _searchedMovies

    //For the based movies ( 3 movies )
    fun assignMovie(movie: Movie?) {
        _movie.value = movie
    }

    fun addMovie(movie: Movie) = viewModelScope.launch {
        repository.addMovie(movie)
    }

    fun deleteMovie(movie: Movie) = viewModelScope.launch {
        repository.deleteMovie(movie)
    }

    fun updateMovie(movie: Movie) = viewModelScope.launch {
        repository.updateMovie(movie)
    }

    fun getMovieById(id: Int): LiveData<Movie> {
        return repository.getMovieById(id)
    }

    fun searchMovies(query: String) = viewModelScope.launch {
        _searchedMovies.value = repository.searchMoviesByTitle(query)
    }
}