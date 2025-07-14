package com.example.watchme.ui.detail_screen

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
import com.example.watchme.ui.MoviesViewModel
import com.example.watchme.R
import com.example.watchme.data.model.Movie
import com.example.watchme.databinding.MovieDetailFragmentBinding
import com.example.watchme.ui.edit_movie.EditMovieBottomSheet
import com.example.watchme.utils.showSuccessToast
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import dagger.hilt.android.AndroidEntryPoint
import com.example.watchme.utils.Error
import com.example.watchme.utils.Loading
import com.example.watchme.utils.Success
import com.example.watchme.utils.autoCleared

@AndroidEntryPoint
class MovieDetailFragment: Fragment() {

    private var binding: MovieDetailFragmentBinding by autoCleared()
    private val viewModel: MoviesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MovieDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupObservers() {
        viewModel.movieDetails.observe(viewLifecycleOwner){
            when (it.status) {
                is Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.detailScrollView.visibility = View.VISIBLE // Corrected ID
                    binding.errorMessage.visibility = View.GONE
                    it.status.data?.let { movie ->
                        updateUi(movie)
                    }
                }
                is Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.detailScrollView.visibility = View.GONE
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.errorMessage.text = it.status.message
                }
                is Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.detailScrollView.visibility = View.GONE
                    binding.errorMessage.visibility = View.GONE
                }
            }
        }
    }

    private fun updateUi(movie: Movie){
        Glide.with(binding.root)
            .load(movie.posterUrl)
            .centerCrop()
            .into(binding.moviePoster)


        val formattedRating = "%.1f".format(movie.rating)
        val movieTitleAndRating = "${movie.title} · ⭐ $formattedRating"
        binding.movieTitleAndRating.text = movieTitleAndRating
        binding.genres.text = movie.genres.joinToString(" · ")
        binding.movieDescription.text = movie.description

        binding.movieDescription.post{
            val lineCount = binding.movieDescription.lineCount
            if (lineCount > 4){
                binding.movieDescription.maxLines = 4
                binding.showMore.visibility = View.VISIBLE
                var isExpanded = false
                binding.showMore.setOnClickListener {
                    isExpanded = !isExpanded
                }
                    if (isExpanded){
                        binding.movieDescription.maxLines = Int.MAX_VALUE
                        binding.showMore.text = getString(R.string.show_less)
                    } else {
                        binding.movieDescription.maxLines = 4
                        binding.showMore.text = getString(R.string.show_more)
                    }
            } else {
                binding.showMore.visibility = View.GONE
            }
        }

        if (movie.images.isNullOrEmpty()){
            binding.imagesCarousel.visibility = View.GONE
        } else {
            binding.imagesCarousel.visibility = View.VISIBLE
            setupImageCarousel(movie.images)
        }

        binding.moreOptionsButton.setOnClickListener { view ->
            showPopupMenu(view, movie)
        }
    }

    private fun setupImageCarousel(imageList: List<String>){
        val recyclerView = binding.imagesCarousel

        //If OnFlingListener is already exist - need to Detach it.
        if (recyclerView.onFlingListener != null){
            recyclerView.onFlingListener = null
        }

        binding.imagesCarousel.layoutManager = CarouselLayoutManager()
        binding.imagesCarousel.adapter = CarouselAdapter(imageList.toMutableList())
        CarouselSnapHelper().attachToRecyclerView(binding.imagesCarousel)
    }

    private fun showPopupMenu(view: View, movie: Movie){
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.movie_detail_more_options_menu, popupMenu.menu)

        try {
            val field = popupMenu.javaClass.getDeclaredField("mPopup")
            field.isAccessible = true
            val menuPopupHelper = field.get(popupMenu)
            val setForceIcons = menuPopupHelper.javaClass.getMethod("setForceShowIcon", Boolean::class.javaPrimitiveType)
            setForceIcons.invoke(menuPopupHelper, true)
        } catch (e: Exception){
            e.printStackTrace()
        }

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.delete_option -> {
                    showDeleteConfirmationDialog(movie)
                    true
                }
                R.id.edit_option -> {
                    EditMovieBottomSheet().show(parentFragmentManager, "EditMovieBottomSheet")
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showDeleteConfirmationDialog(movieToDelete: Movie) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_movie))
            .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_movie))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteMovie(movieToDelete)
                showSuccessToast(requireContext(), getString(R.string.movie_deleted_successfully))
                findNavController().popBackStack()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}