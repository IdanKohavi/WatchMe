package com.example.watchme.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.watchme.data.model.Movie
import com.example.watchme.data.repository.MovieRepo
import com.example.watchme.utils.AppLanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.watchme.utils.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repo: MovieRepo,
    private val languageManager: AppLanguageManager
) : ViewModel() {

    //Language changes observer
    private val _langTrigger = MutableLiveData<String>()

    private val langObserver = Observer<String> { lang ->
        fetchMovies(lang)
    }

    init {
        languageManager.language.observeForever(langObserver)
        _langTrigger.value = languageManager.language.value ?: "en-US"
    }

    //Popular movies with system language reflection.
    val movies: LiveData<Resource<List<Movie>>> = _langTrigger.switchMap { lang ->
        repo.getMovies(lang)
    }

    val topRatedMovies: LiveData<Resource<List<Movie>>> = _langTrigger.switchMap { lang ->
        repo.getTopRatedMovies(lang)
    }

    val upcomingMovies: LiveData<Resource<List<Movie>>> = _langTrigger.switchMap { lang ->
        repo.getUpcomingMovies(lang)
    }

    val favorites: LiveData<List<Movie>> = repo.getFavoriteMovies()
    val searchResults: LiveData<List<Movie>> = repo.searchMoviesLocally("")

    var movieDetails: LiveData<Resource<Movie>> = MutableLiveData()

    private val _posterUri = MutableLiveData<Uri?>()
    val posterUri: LiveData<Uri?> = _posterUri

    private val _imageUris = MutableLiveData<List<Uri>>(emptyList())
    val imageUris: LiveData<List<Uri>> = _imageUris

    fun onFavoriteClick(updatedMovie: Movie) {
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
    private val _searchResultsRemote = MutableLiveData<Resource<List<Movie>>>()
    val searchResultsRemote: LiveData<Resource<List<Movie>>> = _searchResultsRemote

    // New function to search movies remotely with pagination
    fun searchMoviesFromApi(query: String, page: Int = 1) {
        viewModelScope.launch {
            _searchResultsRemote.value = Resource.loading()
            val result = repo.searchMoviesRemotely(query) // ← הוספת page
            _searchResultsRemote.value = result
        }
    }

    //Since language observeForever, we need to remove it, if not - can make memory leak.
    override fun onCleared(){
        super.onCleared()
        languageManager.language.removeObserver(langObserver)
    }

    private fun fetchMovies(lang: String) {
        _langTrigger.value = lang
    }


}
