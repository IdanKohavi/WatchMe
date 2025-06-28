package com.example.watchme.ui.intro_screen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.watchme.R
import com.example.watchme.databinding.IntroScreenFragmentBinding
import androidx.core.content.edit
import com.example.watchme.utils.autoCleared

private const val INTRO_SCREEN_KEY = "intro_screen_shown"

class IntroScreenFragment: Fragment() {

//    private var _binding: IntroScreenFragmentBinding? = null
//    private val binding get() = _binding!!
    private var binding : IntroScreenFragmentBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sharedPrefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        if (sharedPrefs.getBoolean(INTRO_SCREEN_KEY, false)) {
            findNavController().navigate(R.id.action_introScreenFragment_to_homeScreenFragment)
        }

        binding = IntroScreenFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.getStartedButton.setOnClickListener {
            requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .edit() {
                    putBoolean(INTRO_SCREEN_KEY, true)
                }

            findNavController().navigate(R.id.action_introScreenFragment_to_homeScreenFragment)
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}