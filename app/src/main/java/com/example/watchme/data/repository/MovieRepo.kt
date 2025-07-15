package com.example.watchme.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.watchme.data.local_db.MovieDao
import com.example.watchme.data.mappers.GenreMapper
import com.example.watchme.data.mappers.toMovie
import com.example.watchme.data.model.Movie
import com.example.watchme.data.model.MovieDto
import com.example.watchme.data.remote_db.MovieRemoteDataSource
import com.example.watchme.utils.Resource
import com.example.watchme.utils.Success
import com.example.watchme.utils.performFetchingAndSaving
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepo @Inject constructor(
    private val remote: MovieRemoteDataSource,
    private val local: MovieDao,
) {

    private var language = if (Locale.getDefault().language == "he") "he-IL" else "en-US"

    fun getMovies(lang: String): LiveData<Resource<List<Movie>>> {
        language = lang
        return performFetchingAndSaving(
            localDbFetch = { local.getAllMovies() },
            remoteDbFetch = {
                syncGenres()
                val movies = remote.fetchPopularMovies(lang = language)
                Log.d("MovieRepo-lang", "Fetched ${movies.status.data} movies from remote")
                movies
            },
            localDbSave = { moviesDtos ->
                val remoteMovies = moviesDtos.results.map { it.toMovie() }
                val localMovies = local.getAllMoviesSync()
                val favoriteMovies = localMovies.associateBy({ it.id }, { it.isFavorite})
                val mergedMovies = remoteMovies.map {movie ->
                    movie.copy(isFavorite = favoriteMovies[movie.id] == true)
                }
                Log.d("MovieRepo", "Fetched ${mergedMovies.size} movies from remote")
                local.insertMovies(mergedMovies)
            }
        )
    }

    fun getMovieDetails(movieId: Int): LiveData<Resource<Movie>> {
        return performFetchingAndSaving(
            localDbFetch = { local.getMovieById(movieId) },
            remoteDbFetch = { remote.fetchMovieDetails(movieId, language) },
            localDbSave = { movieDto: MovieDto ->
                val remoteMovie = movieDto.toMovie()
                val localMovie = local.getMovieByIdSync(movieId)
                val mergedMovie = if (localMovie != null) {
                    remoteMovie.copy(isFavorite = localMovie.isFavorite)
                } else { remoteMovie}
                local.insertMovie(mergedMovie) }
        )
    }

    fun searchMoviesLocally(query: String): LiveData<List<Movie>> = local.searchMovies(query)

    suspend fun updateFavoriteStatus(movie: Movie) {
        local.updateMovie(movie)
    }
    suspend fun searchMoviesRemotely(query: String): Resource<List<Movie>> {
        val result = remote.searchMovies(query)
        return if (result.status is com.example.watchme.utils.Success) {
            val movies = result.status.data?.results?.map { it.toMovie() } ?: emptyList()
            Resource.success(movies)
        } else {
            Resource.error("Failed to search movies", null)
        }
    }


    suspend fun updateMovie(movie: Movie) {
        local.updateMovie(movie)
    }

    fun getFavoriteMovies(): LiveData<List<Movie>> = local.getFavoriteMovies()

    suspend fun addMovie(movie: Movie) {
        local.insertMovie(movie)
    }

    suspend fun deleteMovie(movie: Movie){
        local.deleteMovie(movie)
    }

    suspend fun syncGenres(){
        val result = remote.fetchGenres()
        if (result.status is Success){
            GenreMapper.setGenreMap(result.status.data?.genres ?: emptyList())
        }
    }
}