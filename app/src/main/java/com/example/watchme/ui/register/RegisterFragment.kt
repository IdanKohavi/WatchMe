package com.example.watchme.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.watchme.databinding.RegisterLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import com.example.watchme.utils.autoCleared

@AndroidEntryPoint
class RegisterFragment : Fragment(){

    private var binding : RegisterLayoutBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RegisterLayoutBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


}