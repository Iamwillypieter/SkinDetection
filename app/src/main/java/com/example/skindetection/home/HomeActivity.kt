package com.example.skindetection.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.skindetection.article.ArticleActivity
import com.example.skindetection.camera.CameraActivity
import com.example.skindetection.databinding.ActivityHomeBinding
import com.example.skindetection.profile.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi View Binding
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Ambil username dari Firestore
        getUsername()

        // Set event klik untuk tombol navigasi
        binding.btnHome.setOnClickListener {
            Toast.makeText(this, "Home Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.btnScan.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.homeCard1.setOnClickListener {
            openArticle("Judul Artikel 1", "Isi artikel pertama yang panjang...")
        }

        binding.homeCard2.setOnClickListener {
            openArticle("Judul Artikel 2", "Isi artikel kedua yang menarik...")
        }

        binding.homeCard3.setOnClickListener {
            openArticle("Judul Artikel 3", "Isi artikel ketiga yang seru...")
        }
        binding.homeCard4.setOnClickListener {
            openArticle("Judul Artikel 3", "Isi artikel ketiga yang seru...")
        }
        binding.homeCard5.setOnClickListener {
            openArticle("Judul Artikel 3", "Isi artikel ketiga yang seru...")
        }
    }

    private fun getUsername() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val username = document.getString("username") ?: "User"
                        binding.greetingText.text = "Hai, $username!" // Tampilkan di TextView
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to fetch username: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun openArticle(title: String, content: String) {
        val intent = Intent(this, ArticleActivity::class.java)
        intent.putExtra("TITLE", title)
        intent.putExtra("CONTENT", content)
        startActivity(intent)
    }
}