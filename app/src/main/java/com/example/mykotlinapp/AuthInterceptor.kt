package com.example.mykotlinapp

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

/**
 * HTTP Interceptor that automatically adds JWT Bearer token to all requests.
 * Retrieves the stored JWT token from SharedPreferences and includes it in the
 * Authorization header for all outgoing requests.
 */
class AuthInterceptor(private val context: Context) : Interceptor {

    private val prefs by lazy {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Get stored JWT token
        val token = prefs.getString("auth_token", null)

        // If no token, proceed with original request
        if (token == null) {
            return chain.proceed(originalRequest)
        }

        // Add Authorization header with Bearer token
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}

