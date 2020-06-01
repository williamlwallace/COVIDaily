package com.example.covid_19

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "headline")
class Headline(
    @PrimaryKey @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "url") val url: String
)