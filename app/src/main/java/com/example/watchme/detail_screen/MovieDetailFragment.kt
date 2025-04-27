package com.example.watchme.detail_screen

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.watchme.MoviesViewModel
import com.example.watchme.R
import com.example.watchme.databinding.MovieDetailFragmentBinding
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper

@Suppress("NAME_SHADOWING")
class MovieDetailFragment: Fragment() {

    private var _binding: MovieDetailFragmentBinding? = null

    private val binding get() = _binding!!

    private val viewModel: MoviesViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = MovieDetailFragmentBinding.inflate(inflater, container, false)


        viewModel.movie.observe(viewLifecycleOwner){ movie ->
            movie?.let {
                val context = binding.root.context
                val resId = context.resources.getIdentifier(movie.posterUrl, "drawable", context.packageName)

                Glide.with(binding.root)
                    .load(resId)
                    .centerCrop()
                    .into(binding.moviePoster)

                val genres = buildString {
                    for ((index, genre) in movie.genres.withIndex()) {
                        append(genre)
                        if (index != movie.genres.lastIndex) {
                            append(" · ")
                        }
                    }
                }
                binding.genres.text = genres

                val titleAndRating = "${movie.title } · ${movie.rating} ⭐"
                binding.movieTitleAndRating.text = titleAndRating

                binding.movieDescription.text = movie.description

                if (movie.images.isNullOrEmpty()) {
                    binding.imagesCarousel.visibility = View.GONE
                    binding.bottomCard.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                } else {
                    binding.imagesCarousel.visibility = View.VISIBLE
                    binding.bottomCard.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    setupImageCarousel(movie.images)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var isDescriptionExpended = false

        binding.showMore.setOnClickListener {
            if (isDescriptionExpended) {
                binding.movieDescription.maxLines = 4
                binding.showMore.text = getString(R.string.show_more)
                isDescriptionExpended = false
            } else {
                binding.movieDescription.maxLines = Int.MAX_VALUE
                binding.showMore.text = getString(R.string.show_less)
                isDescriptionExpended = true
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_movieDetailFragment_to_homeScreenFragment)
        }

        binding.moreOptionsButton.setOnClickListener { view ->
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.menuInflater.inflate(R.menu.movie_detail_more_options_menu, popupMenu.menu)

            try {
                val fields = popupMenu.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.name) {
                        field.isAccessible = true
                        val menuPopupHelper = field.get(popupMenu)
                        val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons = classPopupHelper.getMethod("setForceShowIcon", Boolean::class.javaPrimitiveType)
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_option -> {
                        // Show confirmation dialog when delete option is clicked
                        showDeleteConfirmationDialog()
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupImageCarousel(imageList: List<String>) {
        binding.imagesCarousel.setHasFixedSize(true)
        binding.imagesCarousel.layoutManager = CarouselLayoutManager()
        binding.imagesCarousel.adapter = CarouselAdapter(imageList.toMutableList())
        CarouselSnapHelper().attachToRecyclerView(binding.imagesCarousel)
    }

    private fun showDeleteConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Delete Movie")
            .setMessage("Are you sure you want to delete this movie?")
            .setPositiveButton("Yes") { dialog, which ->
                // Perform the delete action here
                // For example, you can call a method to remove the movie from the list or database
                deleteMovie()
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss() // Dismiss the dialog if user clicks "No"
            }
            .create()

        alertDialog.show()
    }

    private fun deleteMovie() {
        // Implement your delete logic here.
        // For example, you could remove the movie from the list and notify the adapter
        Toast.makeText(requireContext(), "Movie Deleted", Toast.LENGTH_SHORT).show()
    }
}