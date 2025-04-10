package com.example.skindetection.ceo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.skindetection.databinding.ActivityCeoBinding

class CeoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCeoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCeoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Contoh: kasih link ke card-card
        binding.cardDonation.setOnClickListener {
            openLink("https://link.dana.id/minta/2vuklo2qgw1")
        }

        binding.cardLinkedin.setOnClickListener {
            openLink("https://www.linkedin.com/in/willy-pieter-julius-situmorang-109330233/")
        }

        binding.cardDiscord.setOnClickListener {
            openLink("https://discord.com/invite/yourserver")
        }
    }

    private fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}