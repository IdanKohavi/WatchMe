package com.example.watchme.ui.home_screen

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.watchme.R
import com.example.watchme.databinding.DrawerFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import com.example.watchme.utils.autoCleared

@AndroidEntryPoint
class DrawerFragment : DialogFragment(R.layout.drawer_fragment) {
    private lateinit var drawerContent: LinearLayout
    private var binding: DrawerFragmentBinding by autoCleared()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DrawerFragmentBinding.bind(view)
        drawerContent = view.findViewById(R.id.drawerContent)

        val screenWidth = resources.displayMetrics.widthPixels
        val drawerWidth = (screenWidth * 0.6).toInt()
        drawerContent.layoutParams.width = drawerWidth

        val isRtl = view.layoutDirection == View.LAYOUT_DIRECTION_RTL
        (drawerContent.layoutParams as FrameLayout.LayoutParams).gravity =
            if (isRtl) Gravity.END else Gravity.START
        drawerContent.requestLayout()

        val slideIn = AnimationUtils.loadAnimation(requireContext(), if(isRtl) R.anim.slide_in_right else R.anim.slide_in_left)
        drawerContent.startAnimation(slideIn)

        view.setOnClickListener {
            dismissWithAnimation()
        }

        binding.navFavourites.setOnClickListener {
            when (findNavController().currentDestination?.id) {
                R.id.homeScreenFragment -> findNavController().navigate(R.id.action_homeScreenFragment_to_favouritesScreenFragment)
                R.id.topRatedUpcomingScreenFragment -> findNavController().navigate(R.id.action_topRatedUpcomingScreenFragment_to_favouritesScreenFragment)
            }
            dismissWithAnimation()
        }

        binding.navHome.setOnClickListener {
            if (findNavController().currentDestination?.id != R.id.homeScreenFragment) {
                findNavController().popBackStack(R.id.homeScreenFragment, false)
            }
            dismissWithAnimation()
        }

        binding.navTopRatedUpcoming.setOnClickListener {
            when (findNavController().currentDestination?.id) {
                R.id.homeScreenFragment -> findNavController().navigate(R.id.action_homeScreenFragment_to_topRatedUpcomingScreenFragment)
                R.id.favouritesScreenFragment -> findNavController().navigate(R.id.action_favouritesScreenFragment_to_topRatedUpcomingScreenFragment)
            }
            dismissWithAnimation()
        }

        binding.navAbout.setOnClickListener {
            when (findNavController().currentDestination?.id) {
                R.id.homeScreenFragment -> findNavController().navigate(R.id.action_homeScreenFragment_to_aboutScreenFragment)
                R.id.favouritesScreenFragment -> findNavController().navigate(R.id.action_favouritesScreenFragment_to_aboutScreenFragment)
                R.id.topRatedUpcomingScreenFragment -> findNavController().navigate(R.id.action_topRatedUpcomingScreenFragment_to_aboutScreenFragment)
            }
            dismissWithAnimation()
        }
    }

    private fun dismissWithAnimation() {
        val isRtl = view?.layoutDirection == View.LAYOUT_DIRECTION_RTL
        val slideOut = AnimationUtils.loadAnimation(requireContext(), if (isRtl) R.anim.slide_out_right else R.anim.slide_out_left)
        drawerContent.startAnimation(slideOut)

        drawerContent.postDelayed({
            dismiss()
        }, slideOut.duration)
    }
}
