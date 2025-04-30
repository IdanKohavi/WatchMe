package com.example.watchme.edit_movie

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.watchme.MoviesViewModel
import com.example.watchme.R
import com.example.watchme.data.model.Movie
import com.example.watchme.databinding.EditMovieLayoutBinding
import com.example.watchme.utils.showSuccessToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.core.net.toUri

class EditMovieBottomSheet private constructor(private val movieToEdit: Movie) : BottomSheetDialogFragment() {

    private var _binding: EditMovieLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MoviesViewModel by activityViewModels()

    private var posterUri: Uri? = null
    private val imageUris = mutableListOf<Uri>()

    private val selectPosterLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            posterUri = uri
            binding.inputEditMoviePoster.apply {
                text = ""
                icon = ContextCompat.getDrawable(context, R.drawable.check_circle_24px)
            }
        }
    }

    private val selectImagesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            imageUris.clear()
            imageUris.addAll(uris)
            binding.inputEditMovieImages.apply {
                text = ""
                icon = ContextCompat.getDrawable(context, R.drawable.check_circle_24px)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = EditMovieLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateFields(movieToEdit)

        binding.inputEditMoviePoster.setOnClickListener {
            selectPosterLauncher.launch("image/*")
        }

        binding.inputEditMovieImages.setOnClickListener {
            selectImagesLauncher.launch("image/*")
        }

        binding.editMovieSubmitButton.setOnClickListener {
            val updatedMovie = createMovieFromInputs()
            if (updatedMovie != null) {
                viewModel.movies.value?.let { movies ->
                    val updatedMovies = movies.map { if (it.id == updatedMovie.id) updatedMovie else it }
                    viewModel.assignMovies(updatedMovies)
                }
                viewModel.assignMovie(updatedMovie)
                showSuccessToast(requireContext(), "Movie updated successfully!")
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun populateFields(movie: Movie) {
        binding.inputEditMovieTitle.setText(movie.title)
        binding.inputEditMovieRating.setText(movie.rating.toString())
        binding.inputEditMovieDescription.setText(movie.description)

        posterUri = movie.posterUrl.toUri()
        binding.inputEditMoviePoster.icon = ContextCompat.getDrawable(requireContext(), R.drawable.check_circle_24px)

        imageUris.clear()
        movie.images?.forEach { image ->
            try {
                imageUris.add(image.toUri())
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to load image: $image", Toast.LENGTH_SHORT).show()
            }
        }
        if (imageUris.isNotEmpty()) {
            binding.inputEditMovieImages.icon = ContextCompat.getDrawable(requireContext(), R.drawable.check_circle_24px)
        }

        val chips = listOf(
            binding.chipAdventure, binding.chipAction, binding.chipComedy,
            binding.chipDrama, binding.chipThriller, binding.chipHorror,
            binding.chipRomance, binding.chipFantasy, binding.chipScifi
        )
        movie.genres.forEach { genre ->
            chips.find { it.text == genre }?.isChecked = true
        }

        val preselectedCount = chips.count { it.isChecked }
        chips.forEach { chip ->
            chip.isEnabled = preselectedCount < 3 || chip.isChecked
        }

        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val selectedCount = checkedIds.size
            chips.forEach { chip ->
                chip.isEnabled = selectedCount < 3 || chip.isChecked
            }
        }
    }

    private fun createMovieFromInputs(): Movie? {
        val title = binding.inputEditMovieTitle.text.toString().trim()
        val ratingText = binding.inputEditMovieRating.text.toString().trim()
        val description = binding.inputEditMovieDescription.text.toString().trim()
        val genres = mutableListOf<String>()

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
            binding.inputEditMovieTitle.error = "Title is required"
            return null
        }

        if (ratingText.isEmpty()) {
            binding.inputEditMovieRating.error = "Rating is required"
            return null
        }

        val rating = ratingText.toDoubleOrNull()
        if (rating == null || rating < 0 || rating > 10) {
            binding.inputEditMovieRating.error = "Enter a valid rating (0-10)"
            return null
        }

        if (description.isEmpty()) {
            binding.inputEditMovieDescription.error = "Description is required"
            return null
        }

        if (genres.isEmpty()) {
            Toast.makeText(requireContext(), "At least one genre is required.", Toast.LENGTH_SHORT).show()
            return null
        }

        // Use existing poster and images as strings
        val finalPosterUrl = posterUri?.toString() ?: movieToEdit.posterUrl
        val finalImageUrls = if (imageUris.isNotEmpty()) imageUris.map { it.toString() } else null

        return Movie(
            id = movieToEdit.id,
            title = title,
            rating = rating,
            posterUrl = finalPosterUrl,
            description = description,
            genres = genres,
            images = finalImageUrls,
            isFavorite = movieToEdit.isFavorite
        )
    }

    companion object {
        fun newInstance(movie: Movie): EditMovieBottomSheet {
            return EditMovieBottomSheet(movie)
        }
    }
}