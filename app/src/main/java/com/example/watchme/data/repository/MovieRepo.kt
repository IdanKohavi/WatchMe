package com.example.watchme.data.repository

import androidx.lifecycle.LiveData
import com.example.watchme.data.local_data_base.MovieDao
import com.example.watchme.data.model.movie.Movie
import com.example.watchme.data.remote_data_base.MovieService
import com.example.watchme.data.remote_data_base.BaseDataSource
import com.example.watchme.data.remote_data_base.MovieRemoteDataSource
import com.example.watchme.utils.Resource
import com.example.watchme.utils.performFetchingAndSaving
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepo @Inject constructor(
    private val RemoteDataSource : MovieRemoteDataSource,
    private val LocalDataSource: MovieDao,
): BaseDataSource() {


    fun fetchPopularMovies(): LiveData<Resource<List<Movie>>> = performFetchingAndSaving(
        localDbFetch = { LocalDataSource.getAllMovies() },
        remoteDbFetch = { getResult { RemoteDataSource.getPopularMovies() } },
        localDbSave = { response ->
            val genres = fetchGenreMap()
            val movies = response.results.map { dto ->
                val images = getTopImages(dto.id)
                dto.toEntity(genres, images)
            }
            LocalDataSource.addMovies(movies)
        }
    )

    fun fetchTopRatedMovies(): LiveData<Resource<List<Movie>>> = performFetchingAndSaving(
        localDbFetch = { LocalDataSource.getAllMovies() },
        remoteDbFetch = { getResult { RemoteDataSource.getTopRatedMovies() } },
        localDbSave = { response ->
            val genres = fetchGenreMap()
            val movies = response.results.map { dto ->
                val images = getTopImages(dto.id)
                dto.toEntity(genres, images)
            }
            LocalDataSource.addMovies(movies)
        }
    )

    suspend fun searchMoviesByTitle(query: String): Resource<List<Movie>> {
        return getResult {
            val genreMap = fetchGenreMap()
            val response = RemoteDataSource.searchMovies(query)
            val movies = response.results.map { dto ->
                val images = getTopImages(dto.id)
                dto.toEntity(genreMap, images)
            }
            Resource.success(movies)
        }
    }

    private suspend fun fetchGenreMap(): Map<Int, String> {
        return RemoteDataSource.getGenres().genres.associate { it.id to it.name }
    }

    private suspend fun getTopImages(movieId: Int): List<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            RemoteDataSource.getMovieImages(movieId).backdrops.take(3)
                .map { "https://image.tmdb.org/t/p/w500${it.filePath}" }
        } catch (e: Exception) {
            emptyList()
        }
    }
}