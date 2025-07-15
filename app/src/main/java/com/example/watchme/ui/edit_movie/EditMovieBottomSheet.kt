package com.example.watchme.ui.edit_movie

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
import com.example.watchme.databinding.EditMovieLayoutBinding
import com.example.watchme.utils.showSuccessToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.core.net.toUri
import com.google.android.material.chip.Chip
import com.example.watchme.utils.Success
import com.example.watchme.utils.autoCleared

class EditMovieBottomSheet() : BottomSheetDialogFragment() {

    private var binding: EditMovieLayoutBinding by autoCleared()
    private val viewModel: MoviesViewModel by activityViewModels()

    private val selectPosterLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let{
            viewModel.setPosterUri(uri)
        }
    }

    private val selectImagesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris : List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.setImageUris(uris)
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
        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners(){

        binding.inputEditMoviePoster.setOnClickListener {
            selectPosterLauncher.launch("image/*")
        }

        binding.inputEditMovieImages.setOnClickListener {
            selectImagesLauncher.launch("image/*")
        }

        binding.editMovieSubmitButton.setOnClickListener {
            val currentMovie = (viewModel.movieDetails.value?.status as Success).data
            if (currentMovie != null && validateInputs()) {
                createUpdatedMovie(currentMovie)
            }
        }
    }

    private fun setupObservers(){

        viewModel.movieDetails.observe(viewLifecycleOwner){
            when(it.status){
                is Success -> {
                    it.status.data?.let{ movie ->
                        populateFields(movie)
                        viewModel.setPosterUri(movie.posterUrl.toUri())
                        viewModel.setImageUris(movie.images?.mapNotNull { it.toUri() } ?: emptyList())
                    }
                }
                else -> {}
            }
        }

        viewModel.posterUri.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                binding.inputEditMoviePoster.apply {
                    text = ""
                    icon = ContextCompat.getDrawable(requireContext(), R.drawable.check_circle_24px)
                    startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_left))
                }
            }
        }

        viewModel.imageUris.observe(viewLifecycleOwner) { uris ->
            if (uris?.isNotEmpty() == true) {
                binding.inputEditMovieImages.apply {
                    text = ""
                    icon = ContextCompat.getDrawable(requireContext(), R.drawable.check_circle_24px)
                    startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_left))
                }
            }
        }
    }

    private fun populateFields(movie: Movie) {
        binding.inputEditMovieTitle.setText(movie.title)
        binding.inputEditMovieRating.setText(movie.rating.toString())
        binding.inputEditMovieDescription.setText(movie.description)
        setupGenreSelection(movie.genres)
    }

    private fun createUpdatedMovie(originalMovie: Movie) {
        val title = binding.inputEditMovieTitle.text.toString().trim()
        val rating = binding.inputEditMovieRating.text.toString().trim().toDouble()
        val description = binding.inputEditMovieDescription.text.toString().trim()
        val genres = getSelectedGenres()
        val posterUri = viewModel.posterUri.value.toString()
        val imageUris = viewModel.imageUris.value?.map { it.toString() } ?: emptyList()

        val updatedMovie = originalMovie.copy(
            id = originalMovie.id,
            title = title,
            rating = rating,
            description = description,
            genres = genres,
            posterUrl = posterUri,
            images = imageUris,
            isFavorite = originalMovie.isFavorite
        )

        viewModel.updateMovie(updatedMovie)
        showSuccessToast(requireContext(), getString(R.string.movie_updated_successfully))
        dismiss()
    }


    private fun validateInputs(): Boolean {
        var isValid = true

        // Title
        if (binding.inputEditMovieTitle.text.toString().trim().isEmpty()) {
            binding.inputEditMovieTitle.error = getString(R.string.title_is_required)
            isValid = false
        }

        // Rating
        val ratingText = binding.inputEditMovieRating.text.toString().trim()
        if (ratingText.isEmpty()) {
            binding.inputEditMovieRating.error = getString(R.string.rating_is_required)
            isValid = false
        } else {
            val rating = ratingText.toDoubleOrNull() ?: 0.0
            if (rating < 0 || rating > 10) {
                binding.inputEditMovieRating.error = getString(R.string.enter_a_valid_rating_0_10)
                isValid = false
            }
        }

        // Description
        if (binding.inputEditMovieDescription.text.toString().trim().isEmpty()) {
            binding.inputEditMovieDescription.error = getString(R.string.description_is_required)
            isValid = false
        }

        // Genres
        if (getSelectedGenres().isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.at_least_one_genre_is_required), Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Poster
        if (viewModel.posterUri.value == null) {
            binding.inputEditMoviePoster.error = getString(R.string.poster_is_required)
            isValid = false
        }

        return isValid
    }

    private fun getSelectedGenres(): List<String>{
        val genres = mutableListOf<String>()
        val chipMap = mapOf(
            binding.chipAdventure to getString(R.string.adventure),
            binding.chipAction to getString(R.string.action),
            binding.chipComedy to getString(R.string.comedy),
            binding.chipDrama to getString(R.string.drama),
            binding.chipThriller to getString(R.string.thriller),
            binding.chipHorror to getString(R.string.horror),
            binding.chipRomance to getString(R.string.romance),
            binding.chipFantasy to getString(R.string.fantasy),
            binding.chipScifi to getString(R.string.sci_fi)
        )
        chipMap.forEach { (chip, genre) ->
            if (chip.isChecked) genres.add(genre)
        }
        return genres
    }

    private fun setupGenreSelection(genres: List<String>){
        val chipMap = mapOf(
            getString(R.string.adventure) to binding.chipAdventure,
            getString(R.string.action) to binding.chipAction,
            getString(R.string.comedy) to binding.chipComedy,
            getString(R.string.drama) to binding.chipDrama,
            getString(R.string.thriller) to binding.chipThriller,
            getString(R.string.horror) to binding.chipHorror,
            getString(R.string.romance) to binding.chipRomance,
            getString(R.string.fantasy) to binding.chipFantasy,
            getString(R.string.sci_fi) to binding.chipScifi
        )

        //First check
        chipMap.values.forEach {it.isChecked = false}
        genres.forEach{ genre ->
            chipMap[genre]?.isChecked = true
        }

        // limit 3 genres
        val preGenreCount = genres.size
        chipMap.values.forEach{ chip ->
            chip.isEnabled = preGenreCount < 3 || chip.isChecked
        }

        // listener
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val genresCount = checkedIds.size
            val chips = (0 until group.childCount).map {group.getChildAt(it) as Chip}
            chips.forEach { chip ->
                chip.isEnabled = genresCount < 3 || chip.isChecked
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearImageUris()
    }
}