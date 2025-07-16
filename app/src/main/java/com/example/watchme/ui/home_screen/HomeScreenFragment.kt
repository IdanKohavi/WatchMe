package com.example.watchme.ui.home_screen

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.example.watchme.R
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchme.ui.MoviesViewModel
import com.example.watchme.data.model.Movie
import com.example.watchme.databinding.HomeScreenFragmentBinding
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import com.example.watchme.utils.Error
import com.example.watchme.utils.Loading
import com.example.watchme.utils.Success
import com.example.watchme.utils.autoCleared
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*


private var searchJob: Job? = null

@AndroidEntryPoint
class HomeScreenFragment : Fragment(), MovieItemAdapter.ItemListener{

    private var binding: HomeScreenFragmentBinding by autoCleared()
    private val viewModel: MoviesViewModel by activityViewModels()
    private lateinit var movieAdapter : MovieItemAdapter
    private var searchBarOpen: Boolean = false

    private val speechRecognizerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.get(0)

            if (!spokenText.isNullOrBlank()) {
                binding.searchBar.setText(spokenText)
                binding.searchBar.setSelection(spokenText.length)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = HomeScreenFragmentBinding.inflate(inflater, container, false)

        setupRecycler()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
        setupSearch()
    }

    private fun setupRecycler(){
        movieAdapter = MovieItemAdapter(this, viewModel)
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        binding.recycler.apply {
            layoutManager = if (isLandscape) {
                GridLayoutManager(requireContext(), 2)
            } else {
                LinearLayoutManager(requireContext())
            }
            adapter = movieAdapter
        }
    }

    private fun setupObservers(){

        //Observe Movies:
        observeMovies()

        //Observe Search:
        viewModel.searchResults.observe(viewLifecycleOwner) { movies ->
            movieAdapter.submitList(movies)
        }
    }

    private fun setupClickListeners(){

        binding.menuButton.setOnClickListener {
            DrawerFragment().show(parentFragmentManager, "DrawerFragment")
        }


        binding.searchButton.setOnClickListener {
            toggleSearchBar()
        }

        binding.micButton?.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "iw")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a movie name...")
            }
            speechRecognizerLauncher.launch(intent)
        }
    }

    private fun toggleSearchBar() {
        if (!searchBarOpen) {
            binding.searchBarContainer.apply {
                this?.alpha = 0f
                this?.visibility = View.VISIBLE
                this?.animate()?.alpha(1f)?.setDuration(300)?.start()
            }

            binding.searchButton.setImageResource(R.drawable.cancel_24px)
            binding.searchBar.requestFocus()
            showKeyboard(binding.searchBar)

        } else {
            binding.searchBarContainer?.animate()?.alpha(0f)?.setDuration(200)?.withEndAction {
                    binding.searchBarContainer?.visibility = View.GONE
                }?.start()

            binding.searchButton.setImageResource(R.drawable.search_24px)
            binding.searchBar.text.clear()
            binding.searchBar.clearFocus()
            hideKeyboard(binding.searchBar)
        }

        searchBarOpen = !searchBarOpen
    }



    override fun onItemClicked(movie: Movie) {
        viewModel.fetchMovieDetails(movie.id)
        findNavController().navigate(R.id.action_homeScreenFragment_to_movieDetailFragment)
    }

    private fun setupSearch() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    val query = s.toString().trim()

                    if (query.isEmpty()) {
                        viewModel.movies.observe(viewLifecycleOwner) { resource ->
                            when (val status = resource.status) {
                                is Success -> {
                                    binding.emptyStateText.visibility = View.GONE
                                    binding.recycler.visibility = View.VISIBLE
                                    movieAdapter.submitList(status.data)
                                }
                                is Loading -> {
                                    binding.emptyStateText.visibility = View.GONE
                                    binding.recycler.visibility = View.GONE
                                }
                                is Error -> {
                                    binding.recycler.visibility = View.GONE
                                    binding.emptyStateText.visibility = View.VISIBLE
                                }
                            }
                        }
                        return@launch
                    }

                    viewModel.searchMoviesFromApi(query)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        viewModel.searchResultsRemote.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                is Success -> {
                    val movies = cleanTop10RatedMovies((resource.status).data)
                    if (movies.isNotEmpty()) {
                        binding.recycler.visibility = View.VISIBLE
                        binding.emptyStateText.visibility = View.GONE
                        binding.progressBar?.visibility = View.GONE
                        movieAdapter.submitList(movies)
                    } else {
                        binding.recycler.visibility = View.GONE
                        binding.emptyStateText.visibility = View.VISIBLE
                        binding.progressBar?.visibility = View.GONE
                    }
                }
                is Loading -> {
                    binding.recycler.visibility = View.GONE
                    binding.emptyStateText.visibility = View.GONE
                    binding.progressBar?.visibility = View.VISIBLE
                }
                is Error -> {
                    binding.recycler.visibility = View.GONE
                    binding.emptyStateText.visibility = View.VISIBLE
                    binding.progressBar?.visibility = View.GONE
                }
            }
        }
    }

    private fun showKeyboard(view: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(view: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun cleanTop10RatedMovies(movies: List<Movie>?): List<Movie> {
        return movies
            ?.filter { it.rating > 0.0 }
            ?.take(10)
            ?: emptyList()
    }

    private fun observeMovies() {
        viewModel.movies.observe(viewLifecycleOwner) {
            when (it.status) {
                is Success -> {
                    val movies = it.status.data
                    if (!movies.isNullOrEmpty()) {
                        binding.recycler.visibility = View.VISIBLE
                        binding.emptyStateText.visibility = View.GONE
                        binding.progressBar?.visibility = View.GONE
                        movieAdapter.submitList(movies)
                    } else {
                        binding.recycler.visibility = View.GONE
                        binding.emptyStateText.visibility = View.VISIBLE
                        binding.progressBar?.visibility = View.GONE
                    }
                }

                is Error -> {
                    binding.recycler.visibility = View.GONE
                    binding.emptyStateText.visibility = View.VISIBLE
                    binding.progressBar?.visibility = View.GONE
                }

                is Loading -> {
                    binding.recycler.visibility = View.GONE
                    binding.emptyStateText.visibility = View.GONE
                    binding.progressBar?.visibility = View.VISIBLE
                }
            }
        }
    }
}