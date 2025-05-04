package com.example.watchme

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.watchme.data.model.Movie


@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovie(movie: Movie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovies(movies: List<Movie>)

    @Delete
    suspend fun deleteMovie(vararg movie: Movie)

    @Update
    suspend fun updateMovie(movie: Movie)

    @Query("SELECT * FROM movies ORDER BY title ASC")
    fun getAllMovies(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies WHERE id= :id")
    fun getMovieById(id: Int): LiveData<Movie>

}