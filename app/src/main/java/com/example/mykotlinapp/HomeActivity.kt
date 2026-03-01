package com.example.mykotlinapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.mykotlinapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val prefs by lazy {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ApiClient with context for JWT token handling
        ApiClient.initialize(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // בדיקה אם המשתמש מחובר
        updateUIBasedOnLoginState()

        binding.myButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val res = ApiClient.api.ping()
                    withContext(Dispatchers.Main) {
                        binding.helloText.text = "✅ השרת מחובר! ${res.message}"
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        binding.helloText.text = "❌ שגיאה: ${e.message}"
                    }
                }
            }
        }

        binding.goToLoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.goToWeatherButton.setOnClickListener {
            val intent = Intent(this, WeatherActivity::class.java)
            startActivity(intent)
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }
    }

    override fun onResume() {
        super.onResume()
        // עדכון UI בכל פעם שחוזרים למסך
        updateUIBasedOnLoginState()
    }

    private fun updateUIBasedOnLoginState() {
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)
        val username = prefs.getString("username", "") ?: ""

        if (isLoggedIn) {
            // המשתמש מחובר - הצג כפתור מזג אוויר והסתר כפתור התחברות
            binding.goToWeatherButton.visibility = View.VISIBLE
            binding.goToLoginButton.visibility = View.GONE
            binding.logoutButton.visibility = View.VISIBLE
            binding.helloText.text = "שלום, $username! 👋"
        } else {
            // המשתמש לא מחובר - הסתר כפתור מזג אוויר והצג כפתור התחברות
            binding.goToWeatherButton.visibility = View.GONE
            binding.goToLoginButton.visibility = View.VISIBLE
            binding.logoutButton.visibility = View.GONE
            binding.helloText.text = ""
        }
    }

    private fun logout() {
        prefs.edit().apply {
            putBoolean("is_logged_in", false)
            remove("username")
            remove("auth_token")  // ✅ Clear JWT token
            apply()
        }
        updateUIBasedOnLoginState()
    }
}