package com.example.skindetection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.skindetection.databinding.ActivityMainBinding
import com.example.skindetection.home.HomeActivity
import com.example.skindetection.user.LoginActivity
import com.example.skindetection.user.RegisterActivity
import com.example.skindetection.utils.SessionManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cek session login
        if (SessionManager.isSessionValid(this)) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        } else {
            SessionManager.clearSession()
        }

        // Tombol Register
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Tombol Login
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}