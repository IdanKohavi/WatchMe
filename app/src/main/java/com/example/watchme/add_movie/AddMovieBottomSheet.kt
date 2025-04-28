package com.example.watchme.add_movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.watchme.databinding.AddMovieLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddMovieBottomSheet : BottomSheetDialogFragment() {

    private var _binding: AddMovieLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddMovieLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // You can initialize your views here if needed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
