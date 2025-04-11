package com.example.skindetection.home

import android.content.Context
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
            Article(R.drawable.kurap, "Kurap", "Kurap adalah penyakit kulit yang disebabkan oleh infeksi jamur. Umumnya, penyakit ini banyak ditemukan pada kaki dan tangan, terutama di sela-sela jari."),
            Article(R.drawable.eksim, "Eksim", "Eksim adalah jenis penyakit kulit yang paling umum terjadi pada anak-anak, namun juga bisa memengaruhi orang dewasa. Ada dua jenis eksim utama, yaitu dermatitis atopik dan dermatitis kontak. Dermatitis atopik adalah jenis eksim pada anak yang paling umum."),
            Article(R.drawable.lupus, "Lupus", "Lupus adalah penyakit autoimun yang dapat memengaruhi berbagai bagian tubuh, termasuk kulit, sendi, ginjal, dan organ dalam lainnya. Salah satu jenis lupus paling umum adalah lupus eritematosus sistemik (LES) yang biasanya memengaruhi kulit dan sendi."),
            Article(R.drawable.psoriasis, "Psoriasis", "Psoriasis merupakan penyakit autoimun yang menyebabkan pertumbuhan cepat sel-sel kulit. Penyakit ini memunculkan bercak merah, tebal, bersisik yang disebut plak. Plak psoriasis dapat muncul di berbagai bagian tubuh, termasuk kulit kepala, siku, lutut, dan punggung."),
            Article(R.drawable.kutil, "Kutil", "Kutil adalah pertumbuhan kulit yang terjadi karena infeksi Human Papillomavirus (HPV). Penyakit kulit ini biasanya tidak berbahaya, tetapi bisa menimbulkan ketidaknyamanan estetika dan, dalam beberapa kasus dapat menyebabkan rasa sakit atau gatal."),
            Article(R.drawable.kusta, "Kusta", "Kusta adalah penyakit menular yang disebabkan oleh bakteri Mycobacterium leprae. Gejala kusta bervariasi dari ringan hingga parah, tetapi sering melibatkan kulit, saraf, dan selaput lendir. Gejala yang paling umum termasuk kulit kering, kaku, mimisan, otot kaki tangan melemah, dan luka pada telapak kaki."),
            Article(R.drawable.rosacea, "Rosacea", "Kondisi kulit kronis ini ditandai oleh kemerahan, pembengkakan, dan pembuluh darah yang tampak di wajah. Meskipun tidak berbahaya secara medis, rosacea dapat berdampak signifikan pada kualitas hidup seseorang karena gejalanya yang mencolok seringkali berdampak pada kepercayaan diri seseorang."),
            Article(R.drawable.vitiligo, "Vitiligo", "Vitiligo adalah penyakit kulit yang menyebabkan hilangnya pigmentasi pada kulit, biasanya dalam bentuk bercak putih yang tidak berpigmen. Ini terjadi karena kerusakan pada melanosit, sel-sel yang bertanggung jawab untuk menghasilkan pigmen melanin."),
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

    override fun onResume() {
        super.onResume()
        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putLong("lastLoginTime", System.currentTimeMillis())
        editor.apply()
    }

}