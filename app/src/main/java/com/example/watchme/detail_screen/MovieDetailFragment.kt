package com.example.watchme.detail_screen

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.watchme.MoviesViewModel
import com.example.watchme.R
import com.example.watchme.data.model.Movie
import com.example.watchme.databinding.MovieDetailFragmentBinding
import com.example.watchme.edit_movie.EditMovieBottomSheet
import com.example.watchme.utils.showSuccessToast
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper

@Suppress("NAME_SHADOWING")
class MovieDetailFragment: Fragment() {

    private var _binding: MovieDetailFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MoviesViewModel by activityViewModels()
    private var currentMovieID : Int? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = MovieDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.movie.observe(viewLifecycleOwner) { movie ->
            movie?.let {
                currentMovieID = it.id
                updateUi(it)
            }
        }

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
                        showDeleteConfirmationDialog()
                        true
                    }
                    R.id.edit_option -> {
                        viewModel.movie.value?.let { movie ->
                            EditMovieBottomSheet.newInstance(movie).show(parentFragmentManager, "EditMovieBottomSheet")
                        }
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

        binding.imagesCarousel.onFlingListener = null

        binding.imagesCarousel.adapter = CarouselAdapter(imageList.toMutableList())

        CarouselSnapHelper().attachToRecyclerView(binding.imagesCarousel)
    }

    private fun showDeleteConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_movie))
            .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_movie))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                currentMovieID?.let { _ ->
                    viewModel.movie.value?.let { movieToDelete ->
                        viewModel.deleteMovie(movieToDelete)
                        showSuccessToast(requireContext(),
                            getString(R.string.movie_deleted_successfully))
                        findNavController().navigate(R.id.action_movieDetailFragment_to_homeScreenFragment)
                    }
                }
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        alertDialog.show()
    }

    private fun updateUi(movie: Movie){
        Glide.with(binding.root)
            .load(movie.posterUrl)
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

        if(movie.description.length > 200) {
            binding.movieDescription.maxLines = 4
            binding.showMore.visibility = View.VISIBLE
        } else {
            binding.movieDescription.maxLines = Int.MAX_VALUE
            binding.showMore.visibility = View.GONE
        }

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
