package com.example.watchme.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.watchme.R
import com.example.watchme.databinding.AboutLayoutBinding
import com.example.watchme.utils.autoCleared


class AboutScreenFragment: Fragment() {

//    private var _binding: AboutLayoutBinding? = null
//    private val binding get() = _binding!!

    private var binding : AboutLayoutBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AboutLayoutBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_aboutScreenFragment_to_homeScreenFragment)
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}