package com.example.skindetection.home

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
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
import java.io.File

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private fun saveImagePath(path: String) {
        val prefs = getSharedPreferences("scan_history", MODE_PRIVATE)
        val paths = prefs.getStringSet("image_paths", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        paths.add(path)
        prefs.edit().putStringSet("image_paths", paths).apply()
    }

    private fun getSavedImagePaths(): Set<String> {
        val prefs = getSharedPreferences("scan_history", MODE_PRIVATE)
        return prefs.getStringSet("image_paths", emptySet()) ?: emptySet()
    }


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
            saveImagePath(imagePath) // Simpan ke history
        }

        // Tampilkan semua gambar yang tersimpan
        getSavedImagePaths().forEach { path ->
            try {
                val bitmap: Bitmap? = if (path.startsWith("content://")) {
                    val uri = Uri.parse(path)
                    contentResolver.openInputStream(uri)?.use { input ->
                        BitmapFactory.decodeStream(input)
                    }
                } else {
                    val file = File(path)
                    if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
                }

                bitmap?.let { addCardToYourScans(it) }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun addCardToYourScans(bitmap: Bitmap) {
        val cardView = CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(160.dpToPx(), LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                marginEnd = 12.dpToPx()
            }
            radius = 12f
            cardElevation = 6f
        }

        val imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 200.dpToPx()
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageBitmap(bitmap)
        }

        val button = Button(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = "View Result"
            setTextColor(Color.WHITE)
            backgroundTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            addView(imageView)
            addView(button)
        }

        cardView.addView(layout)
        binding.scanCardContainer.addView(cardView)
    }

    // Fungsi dp ke px
    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
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