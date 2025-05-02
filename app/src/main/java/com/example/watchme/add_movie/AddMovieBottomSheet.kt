package com.example.watchme.add_movie

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
import com.example.watchme.MoviesViewModel
import com.example.watchme.R
import com.example.watchme.data.model.Movie
import com.example.watchme.databinding.AddMovieLayoutBinding
import com.example.watchme.utils.showSuccessToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddMovieBottomSheet : BottomSheetDialogFragment() {

    private var _binding: AddMovieLayoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MoviesViewModel by activityViewModels()

    private var posterUri: Uri? = null
    private val imageUris = mutableListOf<Uri>()

    private val selectPosterLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            posterUri = uri
            binding.inputAddMoviePoster.apply {
                text = ""
                icon = ContextCompat.getDrawable(context, R.drawable.check_circle_24px)
            }
            binding.inputAddMoviePoster.startAnimation(
                AnimationUtils.loadAnimation(binding.root.context, R.anim.slide_in_left)
            )
        }
    }

    private val selectImagesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            imageUris.clear()
            imageUris.addAll(uris)
            binding.inputAddMovieImages.apply {
                text = ""
                icon = ContextCompat.getDrawable(context, R.drawable.check_circle_24px)
            }
            binding.inputAddMovieImages.startAnimation(
                AnimationUtils.loadAnimation(binding.root.context, R.anim.slide_in_left)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddMovieLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enforceMaxGenresSelection()

        binding.inputAddMoviePoster.setOnClickListener {
            selectPosterLauncher.launch("image/*")
        }

        binding.inputAddMovieImages.setOnClickListener {
            selectImagesLauncher.launch("image/*")
        }

        binding.addMovieSubmitButton.setOnClickListener {
            val newMovie = createMovieFromInputs()
            if (newMovie != null) {
                viewModel.addMovie(newMovie)
                showSuccessToast(requireContext(), "Movie added successfully!")
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createMovieFromInputs(): Movie? {
        val title = binding.inputAddMovieTitle.text.toString().trim()
        val ratingText = binding.inputAddMovieRating.text.toString().trim()
        val description = binding.inputAddMovieDescription.text.toString().trim()
        val genres = mutableListOf<String>()

        // Collect selected genres
        if (binding.chipAdventure.isChecked) genres.add("Adventure")
        if (binding.chipAction.isChecked) genres.add("Action")
        if (binding.chipComedy.isChecked) genres.add("Comedy")
        if (binding.chipDrama.isChecked) genres.add("Drama")
        if (binding.chipThriller.isChecked) genres.add("Thriller")
        if (binding.chipHorror.isChecked) genres.add("Horror")
        if (binding.chipRomance.isChecked) genres.add("Romance")
        if (binding.chipFantasy.isChecked) genres.add("Fantasy")
        if (binding.chipScifi.isChecked) genres.add("Sci-Fi")

        // Validate inputs
        if (title.isEmpty()) {
            binding.inputAddMovieTitle.error = "Title is required"
            return null
        }

        if (ratingText.isEmpty()) {
            binding.inputAddMovieRating.error = "Rating is required"
            return null
        }

        val rating = ratingText.toDoubleOrNull()
        if (rating == null || rating < 0 || rating > 10) {
            binding.inputAddMovieRating.error = "Enter a valid rating (0-10)"
            return null
        }

        if (description.isEmpty()) {
            binding.inputAddMovieDescription.error = "Description is required"
            return null
        }

        if (genres.isEmpty()) {
            Toast.makeText(requireContext(), "At least one genre is required.", Toast.LENGTH_SHORT).show()
            return null
        }

        if(posterUri == null) {
            binding.inputAddMoviePoster.error = "Poster is required"
            return null
        }

        return Movie(
            title = title,
            rating = rating,
            posterUrl = posterUri.toString(),
            description = description,
            genres = genres,
            images = imageUris.map {it.toString()},
            isFavorite = false
        )
    }

    private fun enforceMaxGenresSelection() {
        val chipGroup = binding.chipGroup
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedCount = checkedIds.size
            val chips = (0 until group.childCount).map { group.getChildAt(it) as com.google.android.material.chip.Chip }

            chips.forEach { chip ->
                chip.isEnabled = selectedCount < 3 || chip.isChecked
            }
        }
    }
}
