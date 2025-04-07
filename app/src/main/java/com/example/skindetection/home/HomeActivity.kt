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
import com.example.skindetection.utils.saveImagePath
import com.example.skindetection.utils.loadSavedImages

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

        // Ambil image path terbaru dari intent
        val imagePath = intent.getStringExtra("image_path")
        if (!imagePath.isNullOrEmpty()) {
            saveImagePath(this, imagePath)
        }

    // Tampilkan gambar-gambar hasil scan sebelumnya
        loadSavedImages(this, binding.scanCardContainer)

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
            Article(R.drawable.jerawat, "Jerawat", "Jerawat adalah masalah kulit yang umum, terjadi saat folikel rambut tersumbat oleh minyak dan sel kulit mati, menyebabkan peradangan dan munculnya benjolan seperti komedo, papul, kista, atau pustula."),
            Article(R.drawable.dermatitis, "Dermatitis", "Dermatitis adalah peradangan kulit yang ditandai dengan ruam, kemerahan, gatal, dan kulit kering. Dermatitis bisa disebabkan oleh berbagai faktor, seperti alergi, genetik, atau paparan zat tertentu. Dermatitis bisa menyerang siapa saja, termasuk bayi."),
            Article(R.drawable.melanoma, "Melanoma", "Melanoma adalah jenis kanker kulit yang sangat serius, berkembang dari sel melanosit (sel yang memproduksi pigmen melanin yang memberi warna pada kulit) dan dapat menyebar ke organ lain."),
            Article(R.drawable.skabies, "Skabies", "Skabies atau kudis adalah penyakit kulit yang disebabkan oleh tungau kecil bernama Sarcoptes scabiei. Tungau ini menggali terowongan di bawah kulit sehingga menimbulkan rasa gatal yang sangat kuat, terutama di malam hari."),
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