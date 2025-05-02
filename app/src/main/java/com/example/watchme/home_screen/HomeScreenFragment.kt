package com.example.watchme.home_screen

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import com.example.watchme.R
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchme.MoviesViewModel
import com.example.watchme.data.model.Movie
import com.example.watchme.databinding.HomeScreenFragmentBinding
import com.example.watchme.add_movie.AddMovieBottomSheet
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import com.getkeepsafe.taptargetview.TapTargetView
import com.getkeepsafe.taptargetview.TapTarget

class HomeScreenFragment : Fragment() {

    private var _binding: HomeScreenFragmentBinding? = null

    private val binding get() = _binding!!

    private val viewModel: MoviesViewModel by activityViewModels()

    var searchBarOpen: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = HomeScreenFragmentBinding.inflate(inflater, container, false)

        setupRecycler()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.movieCount.observe(viewLifecycleOwner) {
            val text = "Your\nMovies(${it})"
            binding.yourMoviesText.text = text
        }

        binding.menuButton.setOnClickListener {
            DrawerFragment().show(parentFragmentManager, "DrawerFragment")
        }

        binding.fab.setOnClickListener {
            AddMovieBottomSheet().show(parentFragmentManager, "AddMovieFragment")
        }

        binding.searchButton.setOnClickListener {
            if (!searchBarOpen) {
                // Show search bar
                binding.searchBar.visibility = View.VISIBLE
                binding.searchBar.startAnimation(
                    AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right)
                )
                binding.searchButton.setImageResource(R.drawable.cancel_24px)
                binding.searchBar.requestFocus()
            } else {
                // Hide search bar
                val slideOut = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_right)
                slideOut.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        binding.searchBar.visibility = View.GONE
                        binding.searchBar.hideKeyboard()
                    }
                })
                binding.searchBar.startAnimation(slideOut)
                binding.searchButton.setImageResource(R.drawable.search_24px)
                binding.searchBar.clearFocus()
            }
            searchBarOpen = !searchBarOpen
        }

        setupSearch()

        showFabHighlight()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecycler() {
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        if(viewModel.movies.value.isNullOrEmpty())
        {
            val dummyMovies = listOf(
                Movie(
                    id = 1,
                    title = "Avengers: Endgame",
                    rating = 8.4,
                    posterUrl = "https://static.wikia.nocookie.net/marvelcinematicuniverse/images/9/91/Endgame_Poster_2.jpg/revision/latest?cb=20190314215527",
                    description = "After the devastating events of Avengers: Infinity War (2018), the universe is in ruins. With the help of remaining allies, the Avengers assemble once more in order to reverse Thanos' actions and restore balance to the universe.",
                    genres = listOf("Action", "Adventure", "Sci-Fi"),
                    images = listOf("https://wallpapercat.com/w/full/6/f/d/32387-1536x2732-samsung-hd-avengers-background-photo.jpg", "https://wallpapercat.com/w/full/b/7/6/119490-1536x2732-iphone-hd-avengers-background.jpg", "https://platform.vox.com/wp-content/uploads/sites/2/chorus/uploads/chorus_asset/file/10727705/avengersendwakanda__1_.jpg?quality=90&strip=all&crop=0%2C0%2C100%2C100&w=750"),
                    isFavorite = false
                ),
                Movie(
                    id = 2,
                    title = "The Batman",
                    rating = 7.9,
                    posterUrl = "https://wallpapercat.com/w/full/1/d/4/146001-2000x3000-samsung-hd-the-batman-2022-wallpaper-image.jpg",
                    description = "When a sadistic serial killer begins murdering key political figures in Gotham, the Batman is forced to investigate the city's hidden corruption and question his family's involvement.",
                    genres = listOf("Action", "Crime", "Drama"),
                    images = listOf("https://wallpapercat.com/w/full/3/5/e/146168-1284x2778-phone-hd-the-batman-2022-wallpaper-photo.jpg", "https://wallpapercat.com/w/full/5/7/9/144879-1125x2436-iphone-hd-the-batman-2022-wallpaper-image.jpg", "https://wallpapercat.com/w/full/f/e/4/34977-1080x2280-phone-hd-the-batman-2022-wallpaper-photo.jpg"),
                    isFavorite = false
                ),
                Movie(
                    id = 3,
                    title = "Interstellar",
                    rating = 8.6,
                    posterUrl = "https://wallpapercat.com/w/full/d/c/2/306082-1080x1920-samsung-full-hd-interstellar-wallpaper.jpg",
                    description = "When Earth becomes uninhabitable in the future, a farmer and ex-NASA pilot, Joseph Cooper, is tasked to pilot a spacecraft, along with a team of researchers, to find a new planet for humans.",
                    genres = listOf("Adventure", "Drama", "Sci-Fi"),
                    images = listOf("https://wallpapercat.com/w/full/3/9/3/2131424-1080x2560-phone-hd-gargantua-interstellar-background-photo.jpg", "https://wallpapercat.com/w/full/5/2/9/306241-1440x2560-mobile-hd-interstellar-background.jpg", "https://wallpapercat.com/w/full/0/f/1/306146-2160x3840-mobile-4k-interstellar-background-photo.jpg", "https://wallpapercat.com/w/full/e/c/f/306232-1536x2732-phone-hd-interstellar-wallpaper-image.jpg", "https://wallpapercat.com/w/full/f/d/5/306268-1242x2208-samsung-hd-interstellar-wallpaper.jpg"),
                    isFavorite = false
                )
            )
            viewModel.assignMovies(dummyMovies)
        }


        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            if (!movies.isNullOrEmpty()) {
                binding.recycler.visibility = View.VISIBLE
                binding.emptyStateText.visibility = View.GONE
                binding.recycler.adapter = MovieItemAdapter(movies, object : MovieItemAdapter.ItemListener {
                    override fun onItemClicked(movie: Movie) {
                        viewModel.assignMovie(movie)
                        findNavController().navigate(R.id.action_homeScreenFragment_to_movieDetailFragment)
                    }
                })
            } else {
                binding.recycler.visibility = View.GONE
                binding.emptyStateText.visibility = View.VISIBLE
                binding.emptyStateText.text = "Click the \"+\" button to add movies."
            }
        }
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun setupSearch() {
        binding.searchBar.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterMovies(s.toString())
            }
        })
    }

    private fun filterMovies(query: String) {
        viewModel.movies.value?.let { movies ->
            val filteredMovies = if (query.isEmpty()) {
                movies
            } else {
                movies.filter { movie ->
                    movie.title.contains(query, ignoreCase = true) ||
                            movie.genres.any { it.contains(query, ignoreCase = true) }
                }
            }
            updateRecyclerView(filteredMovies)
        }
    }

    private fun updateRecyclerView(movies: List<Movie>) {
        if (movies.isNotEmpty()) {
            binding.recycler.visibility = View.VISIBLE
            binding.emptyStateText.visibility = View.GONE
            (binding.recycler.adapter as? MovieItemAdapter)?.updateMovies(movies)
        } else {
            binding.recycler.visibility = View.GONE
            binding.emptyStateText.visibility = View.VISIBLE
            binding.emptyStateText.text = "No movies found"
        }
    }

    private fun showFabHighlight() {
        TapTargetView.showFor(
            requireActivity(),  // activity context
            TapTarget.forView(
                binding.fab,
                "Begin by adding a new movie",
                "Tap here to create your first movie entry"
            )
                .cancelable(true)
                .drawShadow(true)
                .titleTextDimen(R.dimen.tap_target_title_text_size) // Optional if you want custom text size
                .descriptionTextColor(android.R.color.white)
                .outerCircleColor(R.color.colorPrimary) // Customize as needed
                .targetCircleColor(android.R.color.white)
                .tintTarget(true)
                .transparentTarget(true),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)
                    binding.fab.performClick()
                }
            }
        )
    }
}
