package com.example.skindetection.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.skindetection.databinding.ActivityProfileBinding
import com.example.skindetection.home.HomeActivity
import com.example.skindetection.user.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater) // Perbaikan binding
        setContentView(binding.root)

//        binding.textUserName.text = "Willy Pieter Julius Situmorang"
//        binding.textUserEmail.text = "willy@example.com"

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Ambil Data dari Firestore
        getUserData()

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

    private fun getUserData() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val email = document.getString("email") ?: "Email not available"
                        val username = document.getString("username") ?: "Username not available"
                        val profileImageUrl = document.getString("profileImageUrl") ?: ""

                        // Menampilkan data di TextView
                        binding.textUserEmail.text = email
                        binding.textUserName.text = username

                        // Menampilkan foto profil jika ada
                        if (profileImageUrl.isNotEmpty()) {
                            Glide.with(this)
                                .load(profileImageUrl)
                                .into(binding.profileImage)
                        }
                    } else {
                        Toast.makeText(this, "Data user not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to fetch data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
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
        // Menangani proses logout
        FirebaseAuth.getInstance().signOut() // Logout dari Firebase

        // Arahkan pengguna kembali ke halaman login
        val intent = Intent(this, LoginActivity::class.java) // Ganti 'LoginActivity' dengan nama activity login kamu
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Menghapus semua activity sebelumnya
        startActivity(intent)

        // Tampilkan toast
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        finish()
    }
}
