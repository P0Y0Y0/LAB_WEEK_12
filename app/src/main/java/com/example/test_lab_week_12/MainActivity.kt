package com.example.test_lab_week_12

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.test_lab_week_12.model.Movie
import com.google.android.material.snackbar.Snackbar
import java.util.*

class MainActivity : AppCompatActivity(), MovieAdapter.MovieClickListener {

    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movieAdapter = MovieAdapter(this)

        val recyclerView: RecyclerView = findViewById(R.id.movie_list)
        recyclerView.adapter = movieAdapter

        val repo = (application as MovieApplication).movieRepository
        val movieViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return MovieViewModel(repo) as T
                }
            }
        )[MovieViewModel::class.java]

        movieViewModel.popularMovies.observe(this) { movies ->
            val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()

            movieAdapter.addMovies(
                movies
                    .filter { it.releaseDate?.startsWith(currentYear) == true }
                    .sortedByDescending { it.popularity }
            )
        }

        movieViewModel.error.observe(this) { err ->
            if (err.isNotEmpty()) {
                Snackbar.make(recyclerView, err, Snackbar.LENGTH_LONG).show()
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
