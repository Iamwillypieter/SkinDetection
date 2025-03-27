package com.example.skindetection.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.skindetection.R
import com.example.skindetection.databinding.ActivityEditBinding

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBackEdit.setOnClickListener {
            finish()
        }
    }
}