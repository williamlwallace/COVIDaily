package com.example.covid_19.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SavedViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is saved Fragment"
    }
    val text: LiveData<String> = _text
}