package com.example.test_lab_week_12

import com.example.test_lab_week_12.model.Movie
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.test_lab_week_12.api.MovieService

class MovieRepository(private val movieService: MovieService) {

    private val apiKey = "bc30528052dfefd50db660174ae8acad"

    private val movieLiveData = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> = movieLiveData

    private val errorLiveData = MutableLiveData<String>()
    val error: LiveData<String> = errorLiveData

    suspend fun fetchMovies() {
        try {
            val response = movieService.getPopularMovies(apiKey)
            movieLiveData.postValue(response.results)
        } catch (e: Exception) {
            errorLiveData.postValue("An error occurred: ${e.message}")
        }
    }
}
