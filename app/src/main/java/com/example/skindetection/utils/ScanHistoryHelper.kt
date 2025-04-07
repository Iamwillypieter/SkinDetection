package com.example.skindetection.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.skindetection.R
import java.io.File

fun saveImagePath(context: Context, path: String) {
    val prefs = context.getSharedPreferences("scan_history", Context.MODE_PRIVATE)
    val paths = prefs.getStringSet("image_paths", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
    paths.add(path)
    prefs.edit().putStringSet("image_paths", paths).apply()
}

fun getSavedImagePaths(context: Context): Set<String> {
    val prefs = context.getSharedPreferences("scan_history", Context.MODE_PRIVATE)
    return prefs.getStringSet("image_paths", emptySet()) ?: emptySet()
}

fun loadSavedImages(context: Context, container: LinearLayout) {
    getSavedImagePaths(context).forEach { path ->
        try {
            val bitmap: Bitmap? = if (path.startsWith("content://")) {
                val uri = Uri.parse(path)
                context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it)
                }
            } else {
                val file = File(path)
                if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
            }

            bitmap?.let {
                addCardToYourScans(context, container, it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private fun addCardToYourScans(context: Context, container: LinearLayout, bitmap: Bitmap) {
    val cardView = CardView(context).apply {
        layoutParams = LinearLayout.LayoutParams(160.dpToPx(), LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            marginEnd = 12.dpToPx()
        }
        radius = 12f
        cardElevation = 6f
    }

    val imageView = ImageView(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 200.dpToPx()
        )
        scaleType = ImageView.ScaleType.CENTER_CROP
        setImageBitmap(bitmap)
    }

    val button = Button(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        text = "View Result"
        setTextColor(android.graphics.Color.WHITE)
        backgroundTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
    }

    val layout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        addView(imageView)
        addView(button)
    }

    cardView.addView(layout)
    container.addView(cardView)
}

private fun Int.dpToPx(): Int {
    return (this * android.content.res.Resources.getSystem().displayMetrics.density).toInt()
}