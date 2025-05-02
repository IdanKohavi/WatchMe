package com.example.watchme.home_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.watchme.MoviesViewModel
import com.example.watchme.R
import com.example.watchme.data.model.Movie
import com.example.watchme.databinding.MovieCardLayoutBinding

class MovieItemAdapter(private var movies: List<Movie>, private val callBack: ItemListener, private val viewModel: MoviesViewModel) : RecyclerView.Adapter<MovieItemAdapter.MovieItemViewHolder>() {

    interface ItemListener{
        fun onItemClicked(movie: Movie)
    }

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    inner class MovieItemViewHolder(private val binding: MovieCardLayoutBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bind(movie: Movie) {
            // Movie Title
            var title = movie.title
            title = title.replace(" ", "\n")

            // Movie Rating
            binding.movieTitle.text = title
            val movieRating = "⭐ ${movie.rating}"
            binding.movieRating.text = movieRating


            // Movie Poster
            Glide.with(binding.root)
                .load(movie.posterUrl)
                .centerCrop()
                .into(binding.moviePosterImg)

            val favoriteIcon = if (movie.isFavorite) R.drawable.favorite_48px_filled else R.drawable.favorite_48px
            binding.favoriteButton.setImageResource(favoriteIcon)

            binding.favoriteButton.setOnClickListener {
                binding.favoriteButton.startAnimation(
                    AnimationUtils.loadAnimation(binding.root.context, R.anim.scale_animation)
                )

                movie.isFavorite = !movie.isFavorite
                val newIcon = if (movie.isFavorite) R.drawable.favorite_48px_filled else R.drawable.favorite_48px
                binding.favoriteButton.setImageResource(newIcon)

                viewModel.updateMovie(movie)
            }

            binding.root.setOnClickListener(this)
        }




        override fun onClick(v: View?) {
            callBack.onItemClicked(movies[adapterPosition])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieItemViewHolder {
        val binding = MovieCardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieItemViewHolder(binding)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: MovieItemViewHolder, position: Int) {
        holder.bind(movies[position])
    }
}
