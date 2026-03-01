package com.example.mykotlinapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mykotlinapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val TAG = "LoginActivity"

    private val prefs by lazy {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val username = binding.usernameLoginInput.text.toString().trim()
            val password = binding.passwordLoginInput.text.toString()

            Log.d(TAG, "Login button clicked. Username: $username")

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "תמלא שם משתמש וסיסמה", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, "Attempting to login...")

            // Call backend API
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d(TAG, "Calling API...")
                    val res = ApiClient.api.login(LoginRequest(username, password))
                    Log.d(TAG, "API response: ${res.message}")
                    Log.d(TAG, "Token received: ${res.token.take(20)}...")

                    withContext(Dispatchers.Main) {
                        // שמירת מצב התחברות וטוקן JWT
                        prefs.edit().apply {
                            putBoolean("is_logged_in", true)
                            putString("username", username)
                            putString("auth_token", res.token)  // ✅ Store JWT token
                            apply()
                        }

                        Toast.makeText(
                            this@LoginActivity,
                            "✅ התחברות הצליחה: ${res.message}",
                            Toast.LENGTH_LONG
                        ).show()

                        // חזרה למסך הבית
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                } catch (e: retrofit2.HttpException) {
                    Log.e(TAG, "HttpException: ${e.code()} - ${e.message()}")
                    withContext(Dispatchers.Main) {
                        val errorMsg = when (e.code()) {
                            401 -> "❌ שם משתמש או סיסמה שגויים"
                            else -> "❌ שגיאה: ${e.code()}"
                        }
                        Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                } catch (e: java.net.ConnectException) {
                    Log.e(TAG, "ConnectException: ${e.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@LoginActivity,
                            "❌ לא ניתן להתחבר לשרת. האם השרת רץ?",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: java.net.UnknownHostException) {
                    Log.e(TAG, "UnknownHostException: ${e.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@LoginActivity,
                            "❌ לא ניתן למצוא את השרת",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: javax.net.ssl.SSLException) {
                    Log.e(TAG, "SSLException: ${e.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@LoginActivity,
                            "❌ בעיית אבטחה - HTTP לא מאושר",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception: ${e.javaClass.name} - ${e.message}")
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@LoginActivity,
                            "❌ שגיאה: ${e.javaClass.simpleName}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        binding.goToRegisterText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
