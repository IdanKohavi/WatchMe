package com.example.watchme.data.remote_data_base

import com.getkeepsafe.taptargetview.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url()

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("api_key", apiKey)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}