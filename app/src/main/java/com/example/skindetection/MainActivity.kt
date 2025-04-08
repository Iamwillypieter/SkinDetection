package com.example.skindetection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.skindetection.databinding.ActivityMainBinding
import com.example.skindetection.home.HomeActivity
import com.example.skindetection.user.LoginActivity
import com.example.skindetection.user.RegisterActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set OnClickListener untuk tombol Register
        binding.btnRegister.setOnClickListener {
            // Pindah ke RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Set OnClickListener untuk tombol Login
        binding.btnLogin.setOnClickListener {
            // Pindah ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

//        val intent = Intent(this, HomeActivity::class.java)
//        startActivity(intent)
//        finish()
    }
}