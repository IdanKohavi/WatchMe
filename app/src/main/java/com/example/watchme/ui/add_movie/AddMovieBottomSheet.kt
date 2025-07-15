package com.example.watchme.ui.add_movie

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.watchme.ui.MoviesViewModel
import com.example.watchme.R
import com.example.watchme.data.model.Movie
import com.example.watchme.databinding.AddMovieLayoutBinding
import com.example.watchme.utils.showSuccessToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import com.example.watchme.utils.autoCleared

@AndroidEntryPoint
class AddMovieBottomSheet : BottomSheetDialogFragment() {

    private var binding: AddMovieLayoutBinding by autoCleared()
    private val viewModel: MoviesViewModel by activityViewModels()

    private val selectPosterLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let{
            viewModel.setPosterUri(it)
        }
    }

    private val selectImagesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.setImageUris(uris)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddMovieLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupObservers()
        enforceMaxGenresSelection()
    }

    private fun setupClickListeners() {
        binding.inputAddMoviePoster.setOnClickListener {
            selectPosterLauncher.launch("image/*")
        }

        binding.inputAddMovieImages.setOnClickListener {
            selectImagesLauncher.launch("image/*")
        }

        binding.addMovieSubmitButton.setOnClickListener {
            if (validateInputs()) {
                createMovieFromInputs()
            }
        }
    }

    private fun setupObservers() {
        viewModel.posterUri.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                // This is the UX code that provides visual feedback
                binding.inputAddMoviePoster.apply {
                    text = ""
                    icon = ContextCompat.getDrawable(context, R.drawable.check_circle_24px)
                    startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_left))
                }
            }
        }

        viewModel.imageUris.observe(viewLifecycleOwner) { uris ->
            if (uris.isNotEmpty()) {
                // This is the UX code that provides visual feedback
                binding.inputAddMovieImages.apply {
                    text = "" // Clear placeholder text
                    icon = ContextCompat.getDrawable(context, R.drawable.check_circle_24px)
                    startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_left))
                }
            }
        }
    }


    private fun createMovieFromInputs() {
        val title = binding.inputAddMovieTitle.text.toString().trim()
        val rating = binding.inputAddMovieRating.text.toString().trim().toDouble()
        val description = binding.inputAddMovieDescription.text.toString().trim()
        val genres = getSelectedGenres()

        val posterUriString = viewModel.posterUri.value.toString()
        val imageUriStrings = viewModel.imageUris.value?.map { it.toString() } ?: emptyList()

        // Generate a unique negative ID to avoid conflicts with the Tmdb API IDs.
        val uniqueId = -(System.currentTimeMillis().toInt())

        val newMovie = Movie(
            id = uniqueId,
            title = title,
            rating = rating,
            posterUrl = posterUriString,
            description = description,
            genres = genres,
            images = imageUriStrings,
            isFavorite = true
        )

        viewModel.addMovie(newMovie)
        showSuccessToast(requireContext(), getString(R.string.movie_added_successfully))
        dismiss()
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Title
        if (binding.inputAddMovieTitle.text.toString().trim().isEmpty()) {
            binding.inputAddMovieTitle.error = getString(R.string.title_is_required)
            isValid = false
        }

        // Rating
        val ratingText = binding.inputAddMovieRating.text.toString().trim()
        if (ratingText.isEmpty()) {
            binding.inputAddMovieRating.error = getString(R.string.rating_is_required)
            isValid = false
        } else {
            val rating = ratingText.toDoubleOrNull()
            if (rating == null || rating < 0 || rating > 10) {
                binding.inputAddMovieRating.error = getString(R.string.enter_a_valid_rating_0_10)
                isValid = false
            }
        }

        // Description
        if (binding.inputAddMovieDescription.text.toString().trim().isEmpty()) {
            binding.inputAddMovieDescription.error = getString(R.string.description_is_required)
            isValid = false
        }

        // Genres
        if (getSelectedGenres().isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.at_least_one_genre_is_required), Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Poster
        if (viewModel.posterUri.value == null) {
            binding.inputAddMoviePoster.error = getString(R.string.poster_is_required)
            isValid = false
        }

        return isValid
    }

    private fun getSelectedGenres(): List<String> {
        val genres = mutableListOf<String>()
        binding.chipGroup.checkedChipIds.forEach { id ->
            val chip = binding.chipGroup.findViewById<Chip>(id)
            genres.add(chip.text.toString())
        }
        return genres
    }

    private fun enforceMaxGenresSelection() {
        val chipGroup = binding.chipGroup
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedCount = checkedIds.size
            val chips = (0 until group.childCount).map { group.getChildAt(it) as Chip }

            chips.forEach { chip ->
                chip.isEnabled = selectedCount < 3 || chip.isChecked
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearImageUris()
    }
}
