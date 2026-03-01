package com.example.mykotlinapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mykotlinapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }
}