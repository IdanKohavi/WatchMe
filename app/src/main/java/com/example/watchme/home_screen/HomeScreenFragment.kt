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

class HomeScreenFragment : Fragment() {

    private var _binding: HomeScreenFragmentBinding? = null

    private val binding get() = _binding!!

    private val viewModel: MoviesViewModel by activityViewModels()

    private val dummyMovies = listOf(
        Movie(id = 0,  "Avengers Endgame", 8.4, "not implemented", "some desc", listOf("action", "comedy"), null, R.drawable.avangers_image),
        Movie(id = 1,  "The Batman", 7.9, "not implemented", "some desc", listOf("action", "comedy"), null, R.drawable.batman_image),
        Movie(id = 2,  "Interstellar", 8.6, "not yet implmented", "some desc", listOf("action", "comedy"), null, R.drawable.interstellar_image),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = HomeScreenFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = MovieItemAdapter(dummyMovies)
        viewModel.updateMovieCount(dummyMovies.size)

        viewModel.movieCount.observe(viewLifecycleOwner) {
            val text = "Your\nMovies(${it})"
            binding.yourMoviesText.text = text
        }

        binding.menuButton.setOnClickListener {
            DrawerFragment().show(parentFragmentManager, "DrawerFragment")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
