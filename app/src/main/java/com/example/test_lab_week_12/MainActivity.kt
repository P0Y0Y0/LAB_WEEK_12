package com.example.test_lab_week_12

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.test_lab_week_12.model.Movie
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), MovieAdapter.MovieClickListener {

    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val back = findViewById<ImageView>(R.id.back_button)
        val title = findViewById<TextView>(R.id.toolbar_title)

        back.visibility = View.GONE
        title.text = "Popular Movies"

        movieAdapter = MovieAdapter(this)
        val recyclerView: RecyclerView = findViewById(R.id.movie_list)
        recyclerView.adapter = movieAdapter

        val repo = (application as MovieApplication).movieRepository
        val movieViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MovieViewModel(repo) as T
                }
            }
        )[MovieViewModel::class.java]

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    movieViewModel.popularMovies.collect { movies ->
                        movieAdapter.addMovies(movies)
                    }
                }

                launch {
                    movieViewModel.error.collect { err ->
                        if (err.isNotEmpty()) {
                            Snackbar.make(recyclerView, err, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun onMovieClick(movie: Movie) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra(DetailsActivity.EXTRA_TITLE, movie.title)
        intent.putExtra(DetailsActivity.EXTRA_RELEASE, movie.releaseDate)
        intent.putExtra(DetailsActivity.EXTRA_OVERVIEW, movie.overview)
        intent.putExtra(DetailsActivity.EXTRA_POSTER, movie.posterPath)
        startActivity(intent)
    }
}
