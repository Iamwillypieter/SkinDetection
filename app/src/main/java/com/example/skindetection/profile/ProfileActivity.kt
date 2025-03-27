package com.example.skindetection.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.skindetection.databinding.ActivityProfileBinding
import com.example.skindetection.home.HomeActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater) // Perbaikan binding
        setContentView(binding.root)

        binding.textUserName.text = "Willy Pieter Julius Situmorang"
        binding.textUserEmail.text = "willy@example.com"

        binding.buttonBackProfile.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Tombol Edit Profil
        binding.buttonEditProfile.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }
    }

    fun onAboutUsClicked(view: View) {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

    fun onDonationClicked(view: View) {
        Toast.makeText(this, "Donation clicked", Toast.LENGTH_SHORT).show()
    }

    fun onLogoutClicked(view: View) {
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()
    }
}
