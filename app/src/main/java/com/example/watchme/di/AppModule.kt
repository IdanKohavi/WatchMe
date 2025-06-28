package com.example.watchme.di

import com.example.watchme.data.remote_data_base.AuthInterceptor
import com.example.watchme.data.remote_data_base.MovieService
import com.example.watchme.utils.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAuthInterceptor(): AuthInterceptor {
        return AuthInterceptor(Constants.TMDB_API_KEY)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        client: OkHttpClient,
        gson : Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    fun provideMovieService(retrofit: Retrofit): MovieService =
        retrofit.create(MovieService::class.java)

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides

}

