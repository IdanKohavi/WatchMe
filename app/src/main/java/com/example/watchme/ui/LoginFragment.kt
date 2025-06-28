package com.example.watchme.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.watchme.databinding.LoginLayoutBinding
import com.example.watchme.utils.autoCleared

class LoginFragment : Fragment() {

    private var binding : LoginLayoutBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginLayoutBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}