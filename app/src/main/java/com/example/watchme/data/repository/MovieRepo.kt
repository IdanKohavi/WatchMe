package com.example.watchme.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.watchme.data.local_db.MovieDao
import com.example.watchme.data.mappers.GenreMapper
import com.example.watchme.data.mappers.toMovie
import com.example.watchme.data.model.Movie
import com.example.watchme.data.remote_db.MovieRemoteDataSource
import com.example.watchme.utils.Resource
import com.example.watchme.utils.Success
import com.example.watchme.utils.performFetchingAndSaving
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepo @Inject constructor(
    private val remote: MovieRemoteDataSource,
    private val local: MovieDao,
) {

    fun getMovies(): LiveData<Resource<List<Movie>>> {
        return performFetchingAndSaving(
            localDbFetch = { local.getAllMovies() },
            remoteDbFetch = {
                syncGenres()
                remote.fetchPopularMovies() },
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
            remoteDbFetch = { remote.fetchMovieDetails(movieId) },
            localDbSave = { movieDto ->
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

//    private var movieDao: MovieDao?
//    init{
//
//        val db = MovieDB.getDB(application.applicationContext)
//        movieDao = db.movieDao()
//
//    }
//
//    fun getAllMovies() = movieDao?.getAllMovies()
//
//    suspend fun addMovie(movie:Movie) = movieDao?.addMovie(movie)
//
//    suspend fun addMovies(movies: List<Movie>) = movieDao?.addMovies(movies)
//
//    suspend fun deleteMovie(movie:Movie) = movieDao?.deleteMovie(movie)
//
//    suspend fun updateMovie(movie:Movie) = movieDao?.updateMovie(movie)
//
//    fun getMovieById(id:Int) = movieDao?.getMovieById(id)

}