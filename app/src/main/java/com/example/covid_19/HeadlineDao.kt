package com.example.covid_19

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HeadlineDao {

    @Query("SELECT * FROM headline")
    fun getAll() : LiveData<List<Headline>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(headline: Headline)

    @Delete
    suspend fun delete(headline: Headline)
}