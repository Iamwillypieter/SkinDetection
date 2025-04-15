package com.example.skindetection.camera

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.skindetection.databinding.ActivityCameraBinding
import com.example.skindetection.home.HomeActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private var imageUri: Uri? = null
    private var imageFile: File? = null
    private var selectedImagePath: String? = null

    companion object {
        private const val REQUEST_GALLERY = 1
        private const val REQUEST_CAMERA = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol kembali ke HomeActivity
        binding.buttonBackCamera.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Upload dari galeri
        binding.btnUpload.setOnClickListener {
            openGallery()
        }

        // Ambil foto dari kamera
        binding.btnTakePicture.setOnClickListener {
            openCamera()
        }

        // Tombol untuk memproses gambar
        binding.btnProcess.setOnClickListener {
            if (!selectedImagePath.isNullOrEmpty()) {
                // Logika deteksi gambar (ganti dengan logika Anda)
                val detectionResult = "Deteksi: Kulit Sehat" // Contoh hasil deteksi

                // Menampilkan hasil di TextView
                binding.tvDetectionResult.text = detectionResult // Set teks hasil deteksi
                binding.tvDetectionResult.visibility = View.VISIBLE // Menampilkan TextView

                // Jika Anda juga ingin mengirim hasil ke HomeActivity
                 val intent = Intent(this, HomeActivity::class.java)
                 intent.putExtra("image_path", selectedImagePath)
                 intent.putExtra("detection_result", detectionResult) // Kirim hasil
                 startActivity(intent)
            } else {
                Toast.makeText(this, "Silakan pilih atau ambil gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imageFile = createImageFile()
        imageUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            imageFile!!
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_GALLERY -> {
                    val selectedImageUri: Uri? = data?.data
                    selectedImageUri?.let {
                        binding.previewImageView.setImageURI(it)
                        selectedImagePath = it.toString()
                    }
                }
                REQUEST_CAMERA -> {
                    imageUri?.let {
                        binding.previewImageView.setImageURI(it)
                        selectedImagePath = it.toString()
                    }
                }
            }
        }
    }
}