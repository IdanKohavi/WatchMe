package com.example.watchme.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchme.data.model.Movie
import com.example.watchme.data.repository.MovieRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.watchme.utils.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repo: MovieRepo,
) : ViewModel() {

    val movies: LiveData<Resource<List<Movie>>> = repo.getMovies()
    val favorites: LiveData<List<Movie>> = repo.getFavoriteMovies()
    val searchResults: LiveData<List<Movie>> = repo.searchMoviesLocally("")

//  private var _movieDetails = MutableLiveData<Resource<Movie>>()
//  val movieDetails: LiveData<Resource<Movie>> = _movieDetails

    var movieDetails: LiveData<Resource<Movie>> = MutableLiveData()

    private val _posterUri = MutableLiveData<Uri?>()
    val posterUri: LiveData<Uri?> = _posterUri

    private val _imageUris = MutableLiveData<List<Uri>>(emptyList())
    val imageUris: LiveData<List<Uri>> = _imageUris

    fun onFavoriteClick(movie: Movie) {
        val updatedMovie = movie.copy(isFavorite = !movie.isFavorite)
        viewModelScope.launch {
            repo.updateFavoriteStatus(updatedMovie)
        }
    }

    fun updateMovie(movie: Movie) {
        viewModelScope.launch {
            repo.updateMovie(movie)
        }
    }

    fun searchMovie(query: String) : LiveData<List<Movie>> {
        return repo.searchMoviesLocally(query)
    }

    fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            movieDetails = repo.getMovieDetails(movieId)
        }
    }


    fun setPosterUri(uri: Uri) {
        _posterUri.value = uri
    }

    fun setImageUris(uris: List<Uri>) {
        _imageUris.value = uris
    }

    fun clearImageUris(){
        _imageUris.value = emptyList()
        _posterUri.value = null
    }

    fun addMovie(movie: Movie) {
        viewModelScope.launch {
            repo.addMovie(movie)
        }
    }

    fun deleteMovie(movie: Movie) {
        viewModelScope.launch {
            repo.deleteMovie(movie)
        }
    }
}
