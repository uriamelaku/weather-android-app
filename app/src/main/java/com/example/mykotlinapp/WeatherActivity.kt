package com.example.mykotlinapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mykotlinapp.databinding.ActivityWeatherBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding
    private val gson = Gson()

    private val prefs by lazy {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    private val appPrefs by lazy {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
    }

    private var currentWeather: WeatherResponse? = null

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // בדיקת הרשאה - אם המשתמש לא מחובר, חזרה למסך הבית
        if (!appPrefs.getBoolean("is_logged_in", false)) {
            Toast.makeText(this, "עליך להתחבר תחילה", Toast.LENGTH_LONG).show()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerViews()

        binding.searchButton.setOnClickListener {
            val city = binding.cityInput.text?.toString()?.trim().orEmpty()
            if (city.isEmpty()) {
                showError(getString(R.string.error_empty_city))
                return@setOnClickListener
            }
            performSearch(city)
        }

        binding.cityInput.setOnEditorActionListener { _, _, _ ->
            binding.searchButton.performClick()
            true
        }

        binding.historyButton.setOnClickListener {
            toggleHistorySection()
        }

        binding.favoritesButton.setOnClickListener {
            toggleFavoritesSection()
        }

        binding.addFavoriteButton.setOnClickListener {
            addCurrentToFavorites()
        }

        updateLoading(false)
        updateWeatherCard(null)
        updateHistoryDisplay()
        updateFavoritesDisplay()
    }

    private fun setupRecyclerViews() {
        // Setup history RecyclerView
        historyAdapter = HistoryAdapter(
            loadHistory().toMutableList(),
            onItemClick = { city ->
                performSearch(city)
            },
            onItemDelete = { position ->
                val history = loadHistory().toMutableList()
                if (position >= 0 && position < history.size) {
                    history.removeAt(position)
                    saveHistory(history)
                    historyAdapter.removeItem(position)
                    updateHistoryDisplay()
                }
            }
        )
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter

        // Setup favorites RecyclerView
        favoritesAdapter = FavoritesAdapter(
            loadFavorites().toMutableList(),
            onItemClick = { city ->
                performSearch(city)
            },
            onItemDelete = { position ->
                val favorites = loadFavorites().toMutableList()
                if (position >= 0 && position < favorites.size) {
                    val removedCity = favorites[position]
                    removeFavoriteFromServer(position, removedCity)
                }
            }
        )
        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.favoritesRecyclerView.adapter = favoritesAdapter
    }

    private fun toggleHistorySection() {
        val isVisible = binding.historySection.visibility == View.VISIBLE
        binding.historySection.visibility = if (isVisible) View.GONE else View.VISIBLE
    }

    private fun toggleFavoritesSection() {
        val isVisible = binding.favoritesSection.visibility == View.VISIBLE
        binding.favoritesSection.visibility = if (isVisible) View.GONE else View.VISIBLE
    }

    private fun updateHistoryDisplay() {
        val history = loadHistory()
        if (history.isEmpty()) {
            binding.historyRecyclerView.visibility = View.GONE
            binding.historyEmptyView.visibility = View.VISIBLE
            binding.historySection.visibility = View.GONE
        } else {
            binding.historyRecyclerView.visibility = View.VISIBLE
            binding.historyEmptyView.visibility = View.GONE
            historyAdapter.updateItems(history)
        }
    }

    private fun updateFavoritesDisplay() {
        val favorites = loadFavorites()
        if (favorites.isEmpty()) {
            binding.favoritesRecyclerView.visibility = View.GONE
            binding.favoritesEmptyView.visibility = View.VISIBLE
            binding.favoritesSection.visibility = View.GONE
        } else {
            binding.favoritesRecyclerView.visibility = View.VISIBLE
            binding.favoritesEmptyView.visibility = View.GONE
            favoritesAdapter.updateItems(favorites)
        }
    }

    private fun performSearch(city: String) {
        updateLoading(true)
        hideError()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.api.getWeather(city)
                withContext(Dispatchers.Main) {
                    currentWeather = response
                    updateWeatherCard(response)
                    updateHistory(response.city)
                    updateHistoryDisplay()
                }
            } catch (e: retrofit2.HttpException) {
                withContext(Dispatchers.Main) {
                    val message = when (e.code()) {
                        401 -> {
                            // Token expired or invalid - redirect to login
                            logout()
                            getString(R.string.error_session_expired)
                        }
                        404 -> getString(R.string.error_city_not_found)
                        408 -> getString(R.string.error_timeout)
                        else -> getString(R.string.error_server_code, e.code())
                    }
                    showError(message)
                }
            } catch (e: SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    showError(getString(R.string.error_timeout))
                }
            } catch (e: ConnectException) {
                withContext(Dispatchers.Main) {
                    showError(getString(R.string.error_cannot_reach_server))
                }
            } catch (e: UnknownHostException) {
                withContext(Dispatchers.Main) {
                    showError(getString(R.string.error_server_not_found))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError(getString(R.string.error_unexpected, e.javaClass.simpleName))
                }
            } finally {
                withContext(Dispatchers.Main) {
                    updateLoading(false)
                }
            }
        }
    }

    private fun updateWeatherCard(weather: WeatherResponse?) {
        if (weather == null) {
            binding.weatherCard.visibility = View.GONE
            binding.addFavoriteButton.isEnabled = false
            return
        }

        binding.weatherCard.visibility = View.VISIBLE
        binding.addFavoriteButton.isEnabled = true

        binding.cityText.text = getString(R.string.city_country_format, weather.city, weather.country)
        binding.descriptionText.text = translateWeatherDescription(weather.description)
        binding.tempText.text = formatTemp(weather.temp)
        binding.feelsLikeText.text = getString(R.string.feels_like_format, formatTemp(weather.feelsLike))
        binding.humidityText.text = getString(R.string.humidity_format, weather.humidity)
        binding.windText.text = getString(R.string.wind_format, formatWind(weather.windSpeed))
        binding.weatherIcon.setImageResource(getIconRes(weather.icon))
    }

    private fun updateHistory(city: String) {
        val trimmed = city.trim()
        val history = loadHistory().toMutableList()
        history.removeAll { it.city.equals(trimmed, ignoreCase = true) }
        history.add(0, SearchHistoryItem(trimmed, System.currentTimeMillis()))
        while (history.size > 10) {
            history.removeAt(history.size - 1)
        }
        saveHistory(history)
    }

    /**
     * Fetch history from server (supports server v2.0.0+)
     * Falls back to local storage if server call fails
     */
    private fun fetchHistoryFromServer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.api.getHistory()
                val historyItems = response.history.map {
                    SearchHistoryItem(it.city, it.timestamp)
                }
                withContext(Dispatchers.Main) {
                    historyAdapter.updateItems(historyItems)
                    updateHistoryDisplay()
                }
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 401) {
                    withContext(Dispatchers.Main) { logout() }
                }
                // Fallback to local storage
            } catch (e: Exception) {
                // Fallback to local storage on any error
            }
        }
    }

    /**
     * Clear history from server
     */
    private fun clearHistoryFromServer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiClient.api.clearHistory()
                withContext(Dispatchers.Main) {
                    updateHistoryDisplay()
                }
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 401) {
                    withContext(Dispatchers.Main) { logout() }
                }
            } catch (e: Exception) {
                // Ignore errors, local storage will persist
            }
        }
    }

    private fun addCurrentToFavorites() {
        val city = currentWeather?.city ?: return
        val favorites = loadFavorites().toMutableList()

        if (favorites.any { it.equals(city, ignoreCase = true) }) {
            Toast.makeText(this, getString(R.string.favorite_exists, city), Toast.LENGTH_SHORT).show()
            return
        }

        // Try to add via server first
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.api.addFavorite(AddFavoriteRequest(city))
                withContext(Dispatchers.Main) {
                    saveFavorites(response.favorites)
                    Toast.makeText(this@WeatherActivity, getString(R.string.favorite_added, city), Toast.LENGTH_SHORT).show()
                    updateFavoritesDisplay()
                }
            } catch (e: retrofit2.HttpException) {
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        401 -> logout()
                        409 -> Toast.makeText(this@WeatherActivity, getString(R.string.favorite_exists, city), Toast.LENGTH_SHORT).show()
                        else -> {
                            // Fallback to local storage
                            favorites.add(0, city)
                            saveFavorites(favorites)
                            Toast.makeText(this@WeatherActivity, getString(R.string.favorite_added, city), Toast.LENGTH_SHORT).show()
                            updateFavoritesDisplay()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Fallback to local storage on error
                    favorites.add(0, city)
                    saveFavorites(favorites)
                    Toast.makeText(this@WeatherActivity, getString(R.string.favorite_added, city), Toast.LENGTH_SHORT).show()
                    updateFavoritesDisplay()
                }
            }
        }
    }

    /**
     * Fetch favorites from server (supports server v2.0.0+)
     * Falls back to local storage if server call fails
     */
    private fun fetchFavoritesFromServer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.api.getFavorites()
                withContext(Dispatchers.Main) {
                    saveFavorites(response.favorites)
                    favoritesAdapter.updateItems(response.favorites)
                    updateFavoritesDisplay()
                }
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 401) {
                    withContext(Dispatchers.Main) { logout() }
                }
                // Fallback to local storage
            } catch (e: Exception) {
                // Fallback to local storage on any error
            }
        }
    }

    /**
     * Remove a favorite from server
     */
    private fun removeFavoriteFromServer(position: Int, city: String) {
        val favorites = loadFavorites().toMutableList()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiClient.api.removeFavorite(city)
                withContext(Dispatchers.Main) {
                    favorites.removeAt(position)
                    saveFavorites(favorites)
                    favoritesAdapter.removeItem(position)
                    Toast.makeText(
                        this@WeatherActivity,
                        getString(R.string.favorite_removed, city),
                        Toast.LENGTH_SHORT
                    ).show()
                    updateFavoritesDisplay()
                }
            } catch (e: retrofit2.HttpException) {
                withContext(Dispatchers.Main) {
                    if (e.code() == 401) {
                        logout()
                    } else {
                        // Fallback to local removal
                        favorites.removeAt(position)
                        saveFavorites(favorites)
                        favoritesAdapter.removeItem(position)
                        Toast.makeText(
                            this@WeatherActivity,
                            getString(R.string.favorite_removed, city),
                            Toast.LENGTH_SHORT
                        ).show()
                        updateFavoritesDisplay()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Fallback to local removal on error
                    favorites.removeAt(position)
                    saveFavorites(favorites)
                    favoritesAdapter.removeItem(position)
                    Toast.makeText(
                        this@WeatherActivity,
                        getString(R.string.favorite_removed, city),
                        Toast.LENGTH_SHORT
                    ).show()
                    updateFavoritesDisplay()
                }
            }
        }
    }

    private fun updateLoading(isLoading: Boolean) {
        binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.searchButton.isEnabled = !isLoading
        binding.historyButton.isEnabled = !isLoading
        binding.favoritesButton.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        binding.errorText.text = message
        binding.errorText.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.errorText.visibility = View.GONE
    }

    private fun logout() {
        // Clear login state and token
        appPrefs.edit().apply {
            putBoolean("is_logged_in", false)
            remove("username")
            remove("auth_token")
            apply()
        }
        // Navigate to home/login
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun formatTemp(value: Double): String {
        return getString(R.string.temp_format, value)
    }

    private fun formatWind(value: Double): String {
        return getString(R.string.wind_speed_format, value)
    }

    private fun formatTimestamp(timestamp: Long): String {
        val formatter = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    private fun getIconRes(icon: String): Int {
        return when {
            icon.startsWith("01") -> R.drawable.ic_weather_clear
            icon.startsWith("02") || icon.startsWith("03") || icon.startsWith("04") ->
                R.drawable.ic_weather_clouds
            icon.startsWith("09") || icon.startsWith("10") -> R.drawable.ic_weather_rain
            icon.startsWith("11") -> R.drawable.ic_weather_thunder
            icon.startsWith("13") -> R.drawable.ic_weather_snow
            icon.startsWith("50") -> R.drawable.ic_weather_mist
            else -> R.drawable.ic_weather_unknown
        }
    }

    private fun translateWeatherDescription(description: String): String {
        val lowerDesc = description.lowercase().trim()
        return when {
            // Clear sky
            lowerDesc.contains("clear") -> getString(R.string.weather_clear_sky)

            // Clouds
            lowerDesc.contains("few clouds") -> getString(R.string.weather_few_clouds)
            lowerDesc.contains("scattered clouds") -> getString(R.string.weather_scattered_clouds)
            lowerDesc.contains("broken clouds") -> getString(R.string.weather_broken_clouds)
            lowerDesc.contains("overcast") -> getString(R.string.weather_overcast_clouds)
            lowerDesc.contains("clouds") || lowerDesc.contains("cloudy") -> getString(R.string.weather_clouds)

            // Rain
            lowerDesc.contains("light rain") -> getString(R.string.weather_light_rain)
            lowerDesc.contains("moderate rain") -> getString(R.string.weather_moderate_rain)
            lowerDesc.contains("heavy rain") -> getString(R.string.weather_heavy_rain)
            lowerDesc.contains("shower") -> getString(R.string.weather_shower_rain)
            lowerDesc.contains("drizzle") -> getString(R.string.weather_drizzle)
            lowerDesc.contains("rain") -> getString(R.string.weather_rain)

            // Thunderstorm
            lowerDesc.contains("thunder") || lowerDesc.contains("storm") -> getString(R.string.weather_thunderstorm)

            // Snow
            lowerDesc.contains("light snow") -> getString(R.string.weather_light_snow)
            lowerDesc.contains("heavy snow") -> getString(R.string.weather_heavy_snow)
            lowerDesc.contains("snow") -> getString(R.string.weather_snow)

            // Atmosphere
            lowerDesc.contains("mist") -> getString(R.string.weather_mist)
            lowerDesc.contains("fog") -> getString(R.string.weather_fog)
            lowerDesc.contains("haze") -> getString(R.string.weather_haze)
            lowerDesc.contains("smoke") -> getString(R.string.weather_smoke)
            lowerDesc.contains("dust") -> getString(R.string.weather_dust)
            lowerDesc.contains("sand") -> getString(R.string.weather_sand)
            lowerDesc.contains("tornado") -> getString(R.string.weather_tornado)

            // Default
            else -> getString(R.string.weather_unknown)
        }
    }

    private fun loadHistory(): List<SearchHistoryItem> {
        val json = prefs.getString(KEY_HISTORY, "[]")
        val type = object : TypeToken<List<SearchHistoryItem>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    private fun saveHistory(history: List<SearchHistoryItem>) {
        prefs.edit().putString(KEY_HISTORY, gson.toJson(history)).apply()
    }

    private fun loadFavorites(): List<String> {
        val json = prefs.getString(KEY_FAVORITES, "[]")
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    private fun saveFavorites(favorites: List<String>) {
        prefs.edit().putString(KEY_FAVORITES, gson.toJson(favorites)).apply()
    }

    companion object {
        private const val PREFS_NAME = "weather_prefs"
        private const val KEY_HISTORY = "search_history"
        private const val KEY_FAVORITES = "favorites"
    }
}


