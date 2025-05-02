package com.example.watchme.home_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.example.watchme.R
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchme.MoviesViewModel
import com.example.watchme.data.model.Movie
import com.example.watchme.databinding.HomeScreenFragmentBinding
import com.example.watchme.add_movie.AddMovieBottomSheet

class HomeScreenFragment : Fragment() {

    private var _binding: HomeScreenFragmentBinding? = null

    private val binding get() = _binding!!

    private val viewModel: MoviesViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = HomeScreenFragmentBinding.inflate(inflater, container, false)

//        val dummyMovies = listOf(
//            Movie(id = 0,  "Avengers Endgame", 8.4, "avangers_image", getString(R.string.lorem_ipsum), listOf("action", "comedy"), null, false),
//            Movie(id = 1,  "The Batman", 7.9, "batman_image", getString(R.string.lorem_ipsum), listOf("action", "comedy"), null, false),
//            Movie(id = 2,  "Interstellar", 8.6, "interstellar_image", getString(R.string.lorem_ipsum), listOf("action", "comedy"), null, false),
//        )
//
//        viewModel.assignMovies(dummyMovies)
//        viewModel.updateMovieCount(dummyMovies.size)

        setupRecycler()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.allMovies?.observe(viewLifecycleOwner) { movies ->
            val text = "Your\nMovies(${movies.size})"
            binding.yourMoviesText.text = text
        }

        binding.menuButton.setOnClickListener {
//          DrawerFragment().show(parentFragmentManager, "DrawerFragment")
            findNavController().navigate(R.id.action_homeScreenFragment_to_drawerFragment)
        }

        binding.fab.setOnClickListener {
//          AddMovieBottomSheet().show(parentFragmentManager, "AddMovieFragment")
            findNavController().navigate(R.id.action_homeScreenFragment_to_addMovieBottomSheet)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecycler() {


        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        viewModel.allMovies?.observe(viewLifecycleOwner) { movies ->
            if (movies.isNotEmpty()) {
                binding.recycler.visibility = View.VISIBLE
                binding.emptyStateText.visibility = View.GONE
                binding.recycler.adapter = MovieItemAdapter(
                    movies
                ) { movie -> //Lambda for onItemClicked
                    viewModel.assignMovie(movie)
                    findNavController().navigate(R.id.action_homeScreenFragment_to_movieDetailFragment)
                }
            } else {
                binding.recycler.visibility = View.GONE
                binding.emptyStateText.visibility = View.VISIBLE
                binding.emptyStateText.text = "Click the \"+\" button to add movies."
            }
        }

    }
}
