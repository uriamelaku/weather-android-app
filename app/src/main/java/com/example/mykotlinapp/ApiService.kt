package com.example.mykotlinapp

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class PingResponse(val message: String)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val message: String
)

data class RegisterRequest(
    val username: String,
    val password: String
)

data class RegisterResponse(
    val message: String
)

data class WeatherRequest(
    val city: String
)

data class WeatherResponse(
    val city: String,
    val country: String,
    val temp: Double,
    val feelsLike: Double,
    val description: String,
    val humidity: Int,
    val windSpeed: Double,
    val icon: String
)

interface ApiService {
    @GET("ping")
    suspend fun ping(): PingResponse

    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @GET("api/weather")
    suspend fun getWeather(@Query("city") city: String): WeatherResponse
}
