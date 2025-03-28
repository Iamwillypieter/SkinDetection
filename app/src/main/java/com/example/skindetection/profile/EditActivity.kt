package com.example.skindetection.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.skindetection.R
import com.example.skindetection.databinding.ActivityEditBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var imageUri: Uri? = null // Simpan URI gambar yang dipilih

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Ambil data user dari Firestore
        getUserData()

        // Tombol simpan perubahan
        binding.btnSave.setOnClickListener { saveUserData() }

        // Tombol kembali
        binding.buttonBackEdit.setOnClickListener { finish() }

        // Pilih gambar dari galeri
        binding.btnEditProfileImage.setOnClickListener { pickImageFromGallery() }
    }

    // Fungsi untuk memilih gambar dari galeri
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    // Handle hasil pemilihan gambar dari galeri
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            if (imageUri != null) {
                binding.profileImage.setImageURI(imageUri)
            }
        }
    }

    // Fungsi untuk mendapatkan data pengguna dari Firestore
    private fun getUserData() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val username = document.getString("username") ?: ""
                    val email = document.getString("email") ?: ""
                    val phoneNumber = document.getString("phone") ?: ""
                    val profileImageUrl = document.getString("profileImageUrl") ?: ""

                    binding.etName.setText(username)
                    binding.etEmail.setText(email)
                    binding.etPhone.setText(phoneNumber)

                    Glide.with(this)
                        .load(profileImageUrl)
                        .placeholder(R.drawable.ic_profile) // Placeholder jika tidak ada foto
                        .error(R.drawable.ic_profile) // Jika gagal load gambar
                        .into(binding.profileImage)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengambil data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Fungsi untuk menyimpan perubahan data user
    private fun saveUserData() {
        val userId = auth.currentUser?.uid ?: return
        val newUsername = binding.etName.text.toString().trim()
        val newPhoneNumber = binding.etPhone.text.toString().trim()

        if (newUsername.isEmpty() || newPhoneNumber.isEmpty()) {
            Toast.makeText(this, "Username and Mobile Number can't be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri == null) {
            Toast.makeText(this, "Gambar belum dipilih!", Toast.LENGTH_SHORT).show()
            return
        }

        uploadImageAndSaveData(userId, newUsername, newPhoneNumber)
    }

    // Fungsi untuk mengupload gambar ke Firebase Storage lalu menyimpan data ke Firestore

    private fun uploadImageAndSaveData(userId: String, newUsername: String, newPhoneNumber: String) {
        val storageRef = storage.reference.child("profile_images/$userId.jpg")

        imageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        updateUserData(userId, newUsername, newPhoneNumber, downloadUri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal upload gambar: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        } ?: Toast.makeText(this, "Gambar tidak ditemukan!", Toast.LENGTH_SHORT).show()
    }

    // Fungsi untuk update Firestore dengan data baru
    private fun updateUserData(userId: String, username: String, phoneNumber: String, imageUrl: String?) {
        val userData = mutableMapOf<String, Any>(
            "username" to username,
            "phone" to phoneNumber
        )

        if (!imageUrl.isNullOrEmpty()) {
            userData["profileImageUrl"] = imageUrl
        }

        db.collection("users").document(userId).update(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Perubahan berhasil disimpan!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menyimpan perubahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}