package com.example.watchme.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private var currentPage = 1
    private var totalPages = 1


    fun getMovies(lang: String): LiveData<Resource<List<Movie>>> {
        language = lang
        currentPage = 1
        return performFetchingAndSaving(
            localDbFetch = { local.getMoviesByType("top_rated") },
            remoteDbFetch = {
                syncGenres()
                val movies = remote.fetchTopRatedMovies(lang = language)
                totalPages = movies.status.data?.totalPages ?: 1
                Log.d("MovieRepo-lang", "Fetched ${movies.status.data} movies from remote")
                movies
            },
            localDbSave = { moviesDtos ->
                val remoteMovies = moviesDtos.results.map { it.toMovie() }
                val localMovies = local.getAllMoviesSync()
                val localMoviesMap = localMovies.associateBy { it.id }

                //val favoriteMovies = localMovies.associateBy({ it.id }, { it.isFavorite})
                //val movieTypes = localMovies.associateBy({ it.id }, { it.types})
                val mergedMovies = remoteMovies.map { remoteMovie ->
                    val existingMovie = localMoviesMap[remoteMovie.id]
                    val existingTypes = existingMovie?.types ?: emptyList()
                    val newTypes = (existingTypes + "top_rated").distinct() // Add "top_rated" and remove duplicates

                    remoteMovie.copy(
                        isFavorite = existingMovie?.isFavorite == true,
                        types = newTypes
                    )
                }
                Log.d("MovieRepo", "Fetched ${mergedMovies.size} movies from remote")
                //Log.d("MovieRepo,", "Fetched ${movieTypes} types from remote")
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
                    remoteMovie.copy(
                        isFavorite = localMovie.isFavorite,
                        types = localMovie.types
                    )
                } else { remoteMovie}
                local.insertMovie(mergedMovie) }
        )
    }

    suspend fun searchMoviesRemotely(query: String, lang: String): Resource<List<Movie>> {
        val result = remote.searchMovies(query, lang)
        return if (result.status is Success) {
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

    fun getPopularMovies(lang: String): LiveData<Resource<List<Movie>>> {
        language = lang
        return performFetchingAndSaving(
            localDbFetch = { local.getMoviesByType("popular") },
            remoteDbFetch = {
                syncGenres()
                remote.fetchPopularMovies(lang)
            },
            localDbSave = { dtos ->
                val remoteMovies = dtos.results.map { it.toMovie() }
                val localMovies = local.getAllMoviesSync()
                val localMoviesMap = localMovies.associateBy { it.id }

                //val favoriteMovies = localMovies.associateBy({ it.id }, { it.isFavorite})
                //val movieTypes = localMovies.associateBy({ it.id }, { it.types})
                val mergedMovies = remoteMovies.map { remoteMovie ->
                    val existingMovie = localMoviesMap[remoteMovie.id]
                    val existingTypes = existingMovie?.types ?: emptyList()
                    val newTypes = (existingTypes + "popular").distinct() // Add "top_rated" and remove duplicates

                    remoteMovie.copy(
                        isFavorite = existingMovie?.isFavorite == true,
                        types = newTypes
                    )
                }
                local.insertMovies(mergedMovies)
            }
        )
    }

    fun getUpcomingMovies(lang: String): LiveData<Resource<List<Movie>>> {
        language = lang
        return performFetchingAndSaving(
            localDbFetch = { local.getMoviesByType("upcoming") },
            remoteDbFetch = {
                syncGenres()
                remote.fetchUpcomingMovies(lang)
            },
            localDbSave = { dtos ->
                val remoteMovies = dtos.results.map { it.toMovie() }
                val localMovies = local.getAllMoviesSync()
                val localMoviesMap = localMovies.associateBy { it.id }

                //val favoriteMovies = localMovies.associateBy({ it.id }, { it.isFavorite})
                //val movieTypes = localMovies.associateBy({ it.id }, { it.types})
                val mergedMovies = remoteMovies.map { remoteMovie ->
                    val existingMovie = localMoviesMap[remoteMovie.id]
                    val existingTypes = existingMovie?.types ?: emptyList()
                    val newTypes = (existingTypes + "upcoming").distinct() // Add "top_rated" and remove duplicates

                    remoteMovie.copy(
                        isFavorite = existingMovie?.isFavorite == true,
                        types = newTypes
                    )
                }
                local.insertMovies(mergedMovies)
            }
        )
    }

    fun loadMoreMovies(lang: String) : LiveData<Resource<List<Movie>>> {
        Log.d("MovieRepo", "loadMoreMovies called - currentPage: $currentPage, totalPages: $totalPages")
        if (currentPage >= totalPages){
            return MutableLiveData<Resource<List<Movie>>>().apply{
                value = Resource.success(emptyList())
            }
        }

        currentPage++
        language = lang

        return performFetchingAndSaving(
            localDbFetch =  { local.getMoviesByType("top_rated")},
            remoteDbFetch = {
                val movies = remote.fetchTopRatedMovies(lang = language, page = currentPage)
                movies
            },
            localDbSave = { dtos ->
                val remoteMovies = dtos.results.map { it.toMovie() }
                val localMovies = local.getMoviesByTypeSync("top_rated")
                val localMoviesMap = localMovies.associateBy { it.id }
                val mergedMovies = remoteMovies.map { remoteMovie ->
                    val existingMovie = localMoviesMap[remoteMovie.id]
                    val existingTypes = existingMovie?.types ?: emptyList()
                    val newTypes = (existingTypes + "top_rated").distinct()

                    remoteMovie.copy(
                        isFavorite = existingMovie?.isFavorite == true,
                        types = newTypes
                    )
                }
                local.insertMovies(mergedMovies)
            }
        )
    }

    fun hasMorePages(): Boolean = currentPage < totalPages
    fun getCurrentPage(): Int = currentPage
    fun getTotalPages(): Int = totalPages

}