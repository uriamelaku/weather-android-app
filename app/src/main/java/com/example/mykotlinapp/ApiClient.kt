package com.example.mykotlinapp

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Using online server hosted on Render
    private const val BASE_URL = "https://weather-android-app-server.onrender.com/"

    private var authInterceptor: AuthInterceptor? = null

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    /**
     * Initialize the API client with authentication support.
     * Must be called once from your Application or MainActivity.
     */
    fun initialize(context: Context) {
        authInterceptor = AuthInterceptor(context)
    }

    private fun createHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        // Add auth interceptor if available
        authInterceptor?.let { builder.addInterceptor(it) }

        return builder.build()
    }
}

