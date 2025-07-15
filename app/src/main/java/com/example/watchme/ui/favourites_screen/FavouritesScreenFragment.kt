package com.example.watchme.ui.favourites_screen

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchme.ui.MoviesViewModel
import com.example.watchme.R
import com.example.watchme.data.model.Movie
import com.example.watchme.databinding.FavouritesScreenFragmentBinding
import com.example.watchme.ui.home_screen.DrawerFragment
import com.example.watchme.ui.home_screen.MovieItemAdapter
import dagger.hilt.android.AndroidEntryPoint
import com.example.watchme.utils.autoCleared

@AndroidEntryPoint
class FavouritesScreenFragment: Fragment(), MovieItemAdapter.ItemListener {

    private var binding: FavouritesScreenFragmentBinding by autoCleared()
    private val viewModel: MoviesViewModel by activityViewModels()
    private lateinit var movieAdapter: MovieItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FavouritesScreenFragmentBinding.inflate(inflater, container, false)
        setupRecycler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObserver()
        setupClickListeners()

    }

    private fun setupRecycler() {
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

    private fun setupObserver() {

        viewModel.favorites.observe(viewLifecycleOwner) { favoriteMovie ->

            if (favoriteMovie.isNotEmpty()) {
                binding.recycler.visibility = View.VISIBLE
                binding.emptyStateText.visibility = View.GONE
                movieAdapter.submitList(favoriteMovie)
            } else {
                binding.recycler.visibility = View.GONE
                binding.emptyStateText.visibility = View.VISIBLE
                binding.emptyStateText.text = getString(R.string.no_favourite_movies_found)
            }
            binding.yourMoviesText.text = getString(R.string.your_favourites, favoriteMovie.size)
        }
    }

    private fun setupClickListeners(){
        binding.menuButton.setOnClickListener {
            DrawerFragment().show(parentFragmentManager, "DrawerFragment")
        }
    }

    override fun onItemClicked(movie: Movie) {
        viewModel.fetchMovieDetails(movie.id)
        findNavController().navigate(R.id.action_favouritesScreenFragment_to_movieDetailFragment)
    }
}


