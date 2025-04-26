package com.example.watchme.detail_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.watchme.databinding.MovieImageBinding

class CarouselAdapter(private val imageList : MutableList<String>) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    inner class CarouselViewHolder(private val binding : MovieImageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(image: String) {
            Glide.with(binding.root)
                .load(image)
                .centerCrop()
                .into(binding.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        return CarouselViewHolder(MovieImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = imageList.size

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int)  = holder.bind(imageList[position])
}