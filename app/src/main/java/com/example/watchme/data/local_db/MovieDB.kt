package com.example.watchme.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.watchme.data.model.Movie
import com.example.watchme.utils.Converters

@Database(entities = [Movie::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MovieDB : RoomDatabase()  {

    abstract fun movieDao() : MovieDao

    companion object {

        @Volatile
        private var instance: MovieDB? = null

        fun getDB(context: Context): MovieDB = instance ?: synchronized(this) {
            context.deleteDatabase("movies.db")
            Room.databaseBuilder(context.applicationContext, MovieDB::class.java, "movies.db")
                .fallbackToDestructiveMigration()
                .build().also {
                    instance = it
                }
        }

    }
}