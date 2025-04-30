package com.example.watchme.favourites_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchme.MoviesViewModel
import com.example.watchme.R
import com.example.watchme.add_movie.AddMovieBottomSheet
import com.example.watchme.data.model.Movie
import com.example.watchme.databinding.FavouritesScreenFragmentBinding
import com.example.watchme.databinding.HomeScreenFragmentBinding
import com.example.watchme.home_screen.DrawerFragment
import com.example.watchme.home_screen.MovieItemAdapter

class FavouritesScreenFragment: Fragment() {

    private var _binding: FavouritesScreenFragmentBinding? = null

    private val binding get() = _binding!!

    private val viewModel: MoviesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavouritesScreenFragmentBinding.inflate(inflater, container, false)

        setupRecycler()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.menuButton.setOnClickListener {
            DrawerFragment().show(parentFragmentManager, "DrawerFragment")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun setupRecycler() {
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            val favouriteMovies = movies?.filter { it.isFavorite } ?: emptyList()

            if (favouriteMovies.isNotEmpty()) {
                binding.recycler.visibility = View.VISIBLE
                binding.emptyStateText.visibility = View.GONE

                binding.recycler.adapter = MovieItemAdapter(favouriteMovies, object : MovieItemAdapter.ItemListener {
                    override fun onItemClicked(movie: Movie) {
                        viewModel.assignMovie(movie)
                        findNavController().navigate(R.id.action_favouritesScreenFragment_to_movieDetailFragment)
                    }
                })
            } else {
                binding.recycler.visibility = View.GONE
                binding.emptyStateText.visibility = View.VISIBLE
                binding.emptyStateText.text = getString(R.string.no_favourite_movies_found)
            }

            binding.yourMoviesText.text = getString(R.string.your_favourites, favouriteMovies.size.toString())
        }
    }
}


