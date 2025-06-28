package com.example.watchme.ui.edit_movie

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.watchme.ui.MoviesViewModel
import com.example.watchme.R
import com.example.watchme.data.model.movie.Movie
import com.example.watchme.databinding.EditMovieLayoutBinding
import com.example.watchme.utils.showSuccessToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.core.net.toUri
import com.example.watchme.utils.autoCleared

class EditMovieBottomSheet private constructor(private val movieToEdit: Movie) : BottomSheetDialogFragment() {

//    private var _binding: EditMovieLayoutBinding? = null
//    private val binding get() = _binding!!

    private var binding : EditMovieLayoutBinding by autoCleared()
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
        binding = EditMovieLayoutBinding.inflate(inflater, container, false)
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
                viewModel.updateMovie(updatedMovie)
                showSuccessToast(requireContext(), getString(R.string.movie_updated_successfully))
                viewModel.getMovieById(updatedMovie.id)?.observe(viewLifecycleOwner) { updatedMovieFromDb ->
                        viewModel.assignMovie(updatedMovieFromDb)
                        dismiss()
                    }
            }
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

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
                Toast.makeText(requireContext(),
                    getString(R.string.failed_to_load_image, image), Toast.LENGTH_SHORT).show()
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

        if (binding.chipAdventure.isChecked) genres.add(getString(R.string.adventure))
        if (binding.chipAction.isChecked) genres.add(getString(R.string.action))
        if (binding.chipComedy.isChecked) genres.add(getString(R.string.comedy))
        if (binding.chipDrama.isChecked) genres.add(getString(R.string.drama))
        if (binding.chipThriller.isChecked) genres.add(getString(R.string.thriller))
        if (binding.chipHorror.isChecked) genres.add(getString(R.string.horror))
        if (binding.chipRomance.isChecked) genres.add(getString(R.string.romance))
        if (binding.chipFantasy.isChecked) genres.add(getString(R.string.fantasy))
        if (binding.chipScifi.isChecked) genres.add(getString(R.string.sci_fi))

        // Validate inputs
        if (title.isEmpty()) {
            binding.inputEditMovieTitle.error = getString(R.string.title_is_required)
            return null
        }

        if (ratingText.isEmpty()) {
            binding.inputEditMovieRating.error = getString(R.string.rating_is_required)
            return null
        }

        val rating = ratingText.toDoubleOrNull()
        if (rating == null || rating < 0 || rating > 10) {
            binding.inputEditMovieRating.error = getString(R.string.enter_a_valid_rating_0_10)
            return null
        }

        if (description.isEmpty()) {
            binding.inputEditMovieDescription.error = getString(R.string.description_is_required)
            return null
        }

        if (genres.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.at_least_one_genre_is_required), Toast.LENGTH_SHORT).show()
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