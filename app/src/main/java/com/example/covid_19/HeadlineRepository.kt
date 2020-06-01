package com.example.covid_19

import androidx.lifecycle.LiveData


class HeadlineRepository(private val headlineDao: HeadlineDao) {

    val savedHeadlines: LiveData<List<Headline>> = headlineDao.getAll()

    suspend fun insert(headline: Headline) {
        headlineDao.insert(headline)
    }
}