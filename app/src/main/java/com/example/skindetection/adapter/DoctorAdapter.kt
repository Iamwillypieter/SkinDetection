package com.example.skindetection.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skindetection.R
import com.example.skindetection.databinding.ItemDoctorBinding
import com.example.skindetection.utils.data.Doctor

class DoctorAdapter(private val doctorList: List<Doctor>) :
    RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    inner class DoctorViewHolder(val binding: ItemDoctorBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val binding = ItemDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DoctorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctorList[position]
        holder.binding.apply {
            tvDoctorName.text = doctor.name
            tvDoctorDesc.text = doctor.description
            imgDoctorPhoto.setImageResource(doctor.photoResId)
        }
    }

    override fun getItemCount(): Int = doctorList.size
}