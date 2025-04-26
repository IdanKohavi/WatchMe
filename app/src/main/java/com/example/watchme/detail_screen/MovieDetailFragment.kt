package com.example.watchme.detail_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.watchme.databinding.MovieDetailFragmentBinding
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper

class MovieDetailFragment: Fragment() {

    private var _binding: MovieDetailFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = MovieDetailFragmentBinding.inflate(inflater, container, false)

        setupImageCarousel()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupImageCarousel() {
        binding.imagesCarousel.setHasFixedSize(true)
        binding.imagesCarousel.layoutManager = CarouselLayoutManager()
        CarouselSnapHelper().attachToRecyclerView(binding.imagesCarousel)
    }
}