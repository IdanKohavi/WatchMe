package com.example.watchme.home_screen

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.watchme.R
import com.example.watchme.databinding.DrawerFragmentBinding

class DrawerFragment : DialogFragment(R.layout.drawer_fragment) {
    private lateinit var drawerContent: LinearLayout
    private var _binding: DrawerFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DrawerFragmentBinding.bind(view)
        drawerContent = view.findViewById(R.id.drawerContent)

        val screenWidth = resources.displayMetrics.widthPixels
        val drawerWidth = (screenWidth * 0.6).toInt()
        drawerContent.layoutParams.width = drawerWidth
        drawerContent.requestLayout()

        val slideIn = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_left)
        drawerContent.startAnimation(slideIn)

        view.setOnClickListener {
            dismissWithAnimation()
        }

        binding.navFavourites.setOnClickListener {
            if (findNavController().currentDestination?.id != R.id.favouritesScreenFragment) {
                findNavController().navigate(R.id.action_homeScreenFragment_to_favouritesScreenFragment)
            }
            dismissWithAnimation()
        }

        binding.navHome.setOnClickListener {
            if (findNavController().currentDestination?.id != R.id.homeScreenFragment) {
                findNavController().popBackStack(R.id.homeScreenFragment, false)
            }
            dismissWithAnimation()
        }

        binding.navAbout.setOnClickListener {
            when (findNavController().currentDestination?.id) {
                R.id.homeScreenFragment -> findNavController().navigate(R.id.action_homeScreenFragment_to_aboutScreenFragment)
                R.id.favouritesScreenFragment -> findNavController().navigate(R.id.action_favouritesScreenFragment_to_aboutScreenFragment)
            }
            dismissWithAnimation()
        }
    }

    private fun dismissWithAnimation() {
        val slideOut = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_left)
        drawerContent.startAnimation(slideOut)

        drawerContent.postDelayed({
            dismiss()
        }, slideOut.duration)
    }
}
