package com.example.watchme.ui.home_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.watchme.ui.MoviesViewModel
import com.example.watchme.R
import com.example.watchme.data.model.Movie
import com.example.watchme.databinding.MovieCardLayoutBinding


class MovieItemAdapter(
    private val callBack: ItemListener,
    private val viewModel: MoviesViewModel
) : ListAdapter<Movie, MovieItemAdapter.MovieItemViewHolder>(MovieDiffCallback()) {

    interface ItemListener{
        fun onItemClicked(movie: Movie)
    }

    inner class MovieItemViewHolder(private val binding: MovieCardLayoutBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(movie: Movie) {
            // Movie Title
            val formattedTitle = movie.title.replace(" ", "\n")
            binding.movieTitle.text = formattedTitle

            // Movie Rating
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
                val newIcon = if (movie.isFavorite) R.drawable.favorite_48px_filled else R.drawable.favorite_48px
                binding.favoriteButton.setImageResource(newIcon)
                viewModel.onFavoriteClick(movie)
            }
        }




        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION)
                callBack.onItemClicked(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieItemViewHolder {
        val binding = MovieCardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieItemViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            // Items are the same if their unique IDs match.
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            // Contents are the same if the data class objects are equal.
            // This works because Movie is a data class, which automatically implements equals().
            return oldItem == newItem
        }
    }

}
