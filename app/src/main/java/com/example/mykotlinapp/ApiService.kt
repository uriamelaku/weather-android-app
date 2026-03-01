package com.example.mykotlinapp

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class PingResponse(val message: String)

data class LoginRequest(
    val username: String,
    val password: String
)

// Updated: Now includes token and user info
data class LoginResponse(
    val message: String,
    val token: String,
    val user: UserInfo
)

data class UserInfo(
    val id: String,
    val username: String
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
    val icon: String,
    val timestamp: Long = 0
)

// History and Favorites response models
data class HistoryResponse(
    val history: List<HistoryItem>
)

data class HistoryItem(
    val city: String,
    val timestamp: Long
)

data class FavoritesResponse(
    val favorites: List<String>
)

data class AddFavoriteRequest(
    val city: String
)

data class FavoritesModifyResponse(
    val message: String,
    val favorites: List<String>
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

    // History endpoints
    @GET("api/history")
    suspend fun getHistory(): HistoryResponse

    @DELETE("api/history")
    suspend fun clearHistory(): Map<String, String>

    // Favorites endpoints
    @GET("api/favorites")
    suspend fun getFavorites(): FavoritesResponse

    @POST("api/favorites")
    suspend fun addFavorite(@Body request: AddFavoriteRequest): FavoritesModifyResponse

    @DELETE("api/favorites/{city}")
    suspend fun removeFavorite(@Path("city") city: String): FavoritesModifyResponse
}
