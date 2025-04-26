package com.example.watchme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MoviesViewModel(application: Application) : AndroidViewModel(application) {

    private val _movieCount = MutableLiveData<Int>()

    val movieCount: LiveData<Int> get() = _movieCount

    fun updateMovieCount(count: Int) {
        _movieCount.value = count
    }

}