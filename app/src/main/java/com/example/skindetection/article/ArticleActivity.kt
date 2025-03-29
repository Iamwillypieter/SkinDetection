package com.example.skindetection.article

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.skindetection.databinding.ActivityArticleBinding

class ArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("TITLE")
        val content = intent.getStringExtra("CONTENT")

        binding.articleTitle.text = title
        binding.articleContent.text = content
    }
}
