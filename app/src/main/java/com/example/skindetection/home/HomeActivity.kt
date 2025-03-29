package com.example.skindetection.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skindetection.R
import com.example.skindetection.adapter.ArticleAdapter
import com.example.skindetection.article.Article
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

        // Inisialisasi RecyclerView Artikel
        setupArticleRecyclerView()
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

    private fun setupArticleRecyclerView() {
        val articles = listOf(
            Article(R.drawable.jerawat, "Jerawat", "Gangguan kulit akibat pori-pori tersumbat."),
            Article(R.drawable.dermatitis, "Dermatitis", "Peradangan kulit yang menyebabkan gatal."),
            Article(R.drawable.melanoma, "Melanoma", "Jenis kanker kulit yang paling berbahaya."),
            Article(R.drawable.skabies, "Skabies", "Infeksi kulit akibat tungau yang menyebabkan gatal."),
        )

        val adapter = ArticleAdapter(articles) { article ->
            openArticle(article)
        }

        binding.recyclerViewArticles.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewArticles.adapter = adapter
    }

    private fun openArticle(article: Article) {
        val intent = Intent(this, ArticleActivity::class.java)
        intent.putExtra("TITLE", article.title)
        intent.putExtra("CONTENT", article.description)
        intent.putExtra("IMAGE", article.imageResId)
        startActivity(intent)
    }
}