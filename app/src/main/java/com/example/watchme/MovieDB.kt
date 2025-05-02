package com.example.watchme

import android.content.Context
import androidx.room.*
import com.example.watchme.data.model.Movie

@Database(entities = [Movie::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MovieDB : RoomDatabase()  {

    abstract fun movieDao() : MovieDao

    companion object {

        @Volatile
        private var instance: MovieDB? = null

        fun getDB(context: Context): MovieDB = instance ?: synchronized(this) {

            Room.databaseBuilder(context.applicationContext, MovieDB::class.java, "movies.db")
                .allowMainThreadQueries()
                .build()
        }

    }
}