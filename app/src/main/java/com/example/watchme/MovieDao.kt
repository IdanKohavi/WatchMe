package com.example.watchme

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMovie(movie: Movie)

    @Delete
    fun deleteMovie(vararg movie: Movie)

    @Update
    fun updateMovie(movie: Movie)

    @Query("SELECT * FROM movies ORDER BY title ASC")
    fun getMovies(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies WHERE title = :title")
    fun getMovieByTitle(title: String): LiveData<Movie>

}