package com.example.skindetection.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.skindetection.MainActivity
import com.example.skindetection.databinding.ActivitySplashBinding
import com.example.skindetection.home.HomeActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private var binding: ActivitySplashBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Delay sebelum pindah ke MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish() // Tutup SplashScreen
        }, 2000) // 2 detik delay
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}