package com.example.mykotlinapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mykotlinapp.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val username = binding.usernameRegisterInput.text.toString().trim()
            val password = binding.passwordRegisterInput.text.toString()

            // Validation
            if (username.isEmpty()) {
                Toast.makeText(this, "שם משתמש לא יכול להיות ריק", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (password.isEmpty()) {
                Toast.makeText(this, "סיסמה לא יכולה להיות ריקה", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (password.length < 4) {
                Toast.makeText(this, "הסיסמה חייבת להיות לפחות 4 תווים", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, "Attempting to register: $username")

            // Call backend API
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d(TAG, "Calling register API...")
                    val res = ApiClient.api.register(RegisterRequest(username, password))
                    Log.d(TAG, "Register response: ${res.message}")

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "✅ נרשמת בהצלחה! ${res.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        // Navigate to login screen
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } catch (e: retrofit2.HttpException) {
                    Log.e(TAG, "HttpException: ${e.code()} - ${e.message()}")
                    withContext(Dispatchers.Main) {
                        val errorMsg = when (e.code()) {
                            409 -> "❌ שם משתמש זה כבר קיים"
                            else -> "❌ שגיאה: ${e.code()}"
                        }
                        Toast.makeText(this@RegisterActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                } catch (e: java.net.ConnectException) {
                    Log.e(TAG, "ConnectException: ${e.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "❌ לא ניתן להתחבר לשרת",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception: ${e.javaClass.name} - ${e.message}")
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "❌ שגיאה: ${e.javaClass.simpleName}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        binding.goToLoginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

