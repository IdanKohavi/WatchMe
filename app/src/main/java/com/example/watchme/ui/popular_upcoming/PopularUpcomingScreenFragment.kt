package com.example.watchme.ui.popular_upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchme.R
import com.example.watchme.data.model.Movie
import com.example.watchme.databinding.PopularUpcomingScreenFragmentBinding
import com.example.watchme.ui.MoviesViewModel
import com.example.watchme.ui.home_screen.DrawerFragment
import com.example.watchme.ui.home_screen.MovieItemAdapter
import com.example.watchme.utils.Loading
import com.example.watchme.utils.Success
import com.example.watchme.utils.Error
import com.example.watchme.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PopularUpcomingScreenFragment : Fragment(), MovieItemAdapter.ItemListener {

    private var binding: PopularUpcomingScreenFragmentBinding by autoCleared()
    private val viewModel: MoviesViewModel by activityViewModels()

    private lateinit var popularAdapter: MovieItemAdapter
    private lateinit var upcomingAdapter: MovieItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PopularUpcomingScreenFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupObservers()
        setupClickListeners()
    }

    private fun setupAdapters() {
        popularAdapter = MovieItemAdapter(this, viewModel)
        upcomingAdapter = MovieItemAdapter(this, viewModel)

        binding.popularRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = popularAdapter
        }

        binding.upcomingRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingAdapter
        }
    }

    private fun setupObservers() {
        viewModel.popularMovies.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                is Loading -> Unit
                is Success -> {
                    popularAdapter.submitList(result.status.data)
                    toggleEmptyState()
                }
                is Error -> {
                    Toast.makeText(requireContext(), "Failed to load top-rated movies", Toast.LENGTH_SHORT).show()
                    toggleEmptyState()
                }
            }
        }

        viewModel.upcomingMovies.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                is Loading -> Unit
                is Success -> {
                    upcomingAdapter.submitList(result.status.data)
                    toggleEmptyState()
                }
                is Error -> {
                    Toast.makeText(requireContext(), "Failed to load upcoming movies", Toast.LENGTH_SHORT).show()
                    toggleEmptyState()
                }
            }
        }
    }

    private fun toggleEmptyState() {
        val top = popularAdapter.currentList
        val up = upcomingAdapter.currentList
        val isEmpty = top.isEmpty() && up.isEmpty()

        binding.emptyStateText.visibility = if (isEmpty) View.VISIBLE else View.GONE
        if (isEmpty) {
            binding.emptyStateText.text = getString(R.string.no_movies_found)
        }
    }

    private fun setupClickListeners() {
        binding.menuButton.setOnClickListener {
            DrawerFragment().show(parentFragmentManager, "DrawerFragment")
        }
    }

    override fun onItemClicked(movie: Movie) {
        viewModel.fetchMovieDetails(movie.id)
        findNavController().navigate(R.id.action_popularUpcomingScreenFragment_to_movieDetailFragment)
    }
}