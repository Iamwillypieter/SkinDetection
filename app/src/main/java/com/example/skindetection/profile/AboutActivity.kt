package com.example.skindetection.profile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.skindetection.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol untuk membuka profil media sosial
        binding.imageInstagram.setOnClickListener {
            openSocialMedia("https://www.instagram.com/iamwillypieters")
        }

        binding.imageWhatsapp.setOnClickListener {
            openSocialMedia("https://api.whatsapp.com/send?phone=081266088224")
        }

        binding.imageFacebook.setOnClickListener {
            openSocialMedia("https://www.facebook.com/WillyPieter")
        }
    }
    private fun openSocialMedia(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}