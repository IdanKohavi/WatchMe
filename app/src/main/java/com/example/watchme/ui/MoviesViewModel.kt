package com.example.watchme.ui

import android.net.Uri
import android.util.Log
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

    private val _loadMoreTrigger = MutableLiveData<String>()
    private val _isLoadingMore = MutableLiveData<Boolean>()

    val loadMoreMovies: LiveData<Resource<List<Movie>>> = _loadMoreTrigger.switchMap { lang ->
        _isLoadingMore.postValue(true)
        repo.loadMoreMovies(lang).also {
            _isLoadingMore.postValue(false)
        }
    }

    val isLoadingMore : LiveData<Boolean> = _isLoadingMore

    private val langObserver = Observer<String> { lang ->
        loadMovies(lang)
    }

    init {
        languageManager.language.observeForever(langObserver)
        _langTrigger.value = languageManager.language.value ?: "en-US"
    }

    val movies: LiveData<Resource<List<Movie>>> = _langTrigger.switchMap { lang ->
        repo.getMovies(lang)
    }

    val popularMovies: LiveData<Resource<List<Movie>>> = _langTrigger.switchMap { lang ->
        repo.getPopularMovies(lang)
    }

    val upcomingMovies: LiveData<Resource<List<Movie>>> = _langTrigger.switchMap { lang ->
        repo.getUpcomingMovies(lang)
    }

    val favorites: LiveData<List<Movie>> = repo.getFavoriteMovies()

    var movieDetails: LiveData<Resource<Movie>> = MutableLiveData()

    private val _posterUri = MutableLiveData<Uri?>()
    val posterUri: LiveData<Uri?> = _posterUri

    private val _imageUris = MutableLiveData<List<Uri>>(emptyList())
    val imageUris: LiveData<List<Uri>> = _imageUris

    fun onFavoriteClick(updatedMovie: Movie) {
        viewModelScope.launch {
            if(updatedMovie.isFavorite) {
                repo.addMovie(updatedMovie)
            } else {
                repo.updateMovie(updatedMovie)
            }
        }
    }

    fun updateMovie(movie: Movie) {
        viewModelScope.launch {
            repo.updateMovie(movie)
        }
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

    fun searchMoviesFromApi(query: String) {
        viewModelScope.launch {
            _searchResultsRemote.value = Resource.loading()
            val result = repo.searchMoviesRemotely(query, languageManager.language.value ?: "en-US")
            _searchResultsRemote.value = result
        }
    }

    override fun onCleared(){
        super.onCleared()
        languageManager.language.removeObserver(langObserver)
    }

    fun loadMovies(lang: String) {
        _langTrigger.value = lang
    }

    fun loadMore(lang: String){
        Log.d("HomeViewModelLoad", "loadMore called with lang: $lang")
        if (_isLoadingMore.value == true) return
        _loadMoreTrigger.value = lang
    }

    fun hasMorePages(): Boolean = repo.hasMorePages()

    fun getLanguage() : String{
        return languageManager.getLanguage()
    }




}
