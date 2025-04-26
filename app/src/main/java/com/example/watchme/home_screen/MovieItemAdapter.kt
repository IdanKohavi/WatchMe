package com.example.watchme.home_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.watchme.data.model.Movie
import com.example.watchme.databinding.MovieCardLayoutBinding

class MovieItemAdapter(private val movies: List<Movie>) : RecyclerView.Adapter<MovieItemAdapter.MovieItemViewHolder>() {

    inner class MovieItemViewHolder(private val binding: MovieCardLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
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
                .load(movie.posterResId) // Temporary -> Will be replaced with posterUrl afted db is implemented
                .centerCrop()
                .into(binding.moviePosterImg)


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
