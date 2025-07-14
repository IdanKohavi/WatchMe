package com.example.watchme.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.watchme.data.model.Movie

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies")
    fun getAllMovies(): LiveData<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    @Update
    suspend fun updateMovie(movie: Movie)

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovieById(id: Int): LiveData<Movie>

    @Query("SELECT * FROM movies WHERE isFavorite = 1")
    fun getFavoriteMovies(): LiveData<List<Movie>>

    @Query("SELECT id FROM movies WHERE isFavorite = 1")
    suspend fun getFavoriteMovieIds(): List<Int>

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%'")
    fun searchMovies(query: String): LiveData<List<Movie>>
}
