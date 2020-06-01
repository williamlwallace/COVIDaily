package com.example.covid_19.ui.saved

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.covid_19.AppDatabase
import com.example.covid_19.Headline
import com.example.covid_19.HeadlineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HeadlineRepository
    val savedHeadlines: LiveData<List<Headline>>

    init {
        val headlineDao = AppDatabase.getDatabase(application, viewModelScope).headlineDao()
        repository = HeadlineRepository(headlineDao)
        savedHeadlines = repository.savedHeadlines
    }

    fun insert(headline: Headline) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(headline)
    }
}