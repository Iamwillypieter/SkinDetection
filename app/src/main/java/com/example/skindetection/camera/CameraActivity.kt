package com.example.skindetection.camera

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.example.skindetection.R
import com.example.skindetection.databinding.ActivityCameraBinding
import com.example.skindetection.home.HomeActivity

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    companion object {
        private const val REQUEST_GALLERY = 1
        private const val REQUEST_CAMERA = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol untuk kembali ke HomeActivity
        binding.buttonBackCamera.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Opsional, supaya activity ini ditutup
        }

        // Klik tombol Upload Image
        binding.btnUpload.setOnClickListener {
            openGallery()
        }

        // Klik tombol Take Picture
        binding.btnTakePicture.setOnClickListener {
            openCamera()
        }
    }

    // Fungsi untuk membuka galeri
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    // Fungsi untuk membuka kamera
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    // Menangani hasil dari galeri/kamera
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_GALLERY -> {
                    val selectedImageUri: Uri? = data?.data
                    selectedImageUri?.let {
                        binding.previewImageView.setImageURI(it) // Menampilkan gambar dari galeri
                    }
                }
                REQUEST_CAMERA -> {
                    val photo: Bitmap? = data?.extras?.get("data") as? Bitmap
                    photo?.let {
                        binding.previewImageView.setImageBitmap(it) // Menampilkan foto dari kamera
                    }
                }
            }
        }
    }
}