package com.example.skindetection.home

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.skindetection.databinding.ActivityViewResultBinding
import java.io.File

class ViewResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imagePath = intent.getStringExtra("image_path")
        val detectionResult = intent.getStringExtra("detection_result")

        imagePath?.let {
            val bitmap = if (it.startsWith("content://")) {
                val uri = Uri.parse(it)
                contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it)
                }
            } else {
                val file = File(it)
                if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
            }

            binding.resultImageView.setImageBitmap(bitmap)
        }

        binding.resultTextView.text = detectionResult
    }
}