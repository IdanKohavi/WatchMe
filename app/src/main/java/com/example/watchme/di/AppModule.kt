package com.example.watchme.di

import android.content.Context
import com.example.watchme.data.local_db.MovieDB
import com.example.watchme.data.local_db.MovieDB.Companion.getDB
import com.example.watchme.data.remote_db.AuthInterceptor
import com.example.watchme.data.remote_db.MovieRemoteDataSource
import com.example.watchme.data.remote_db.TmdbApi
import com.example.watchme.utils.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor {
        return AuthInterceptor(Constants.TMDB_API_KEY)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        client : OkHttpClient,
        gson : Gson
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideTmdbApi(retrofit: Retrofit): TmdbApi =
        retrofit.create(TmdbApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): MovieDB =
        getDB(appContext)

    @Provides
    fun provideMovieDao(db: MovieDB) = db.movieDao()

    @Provides
    @Singleton
    fun provideMovieRemoteDataSource(api: TmdbApi): MovieRemoteDataSource {
        return MovieRemoteDataSource(api)
    }

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder().create()
}