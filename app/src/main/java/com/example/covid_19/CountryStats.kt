package com.example.covid_19

data class CountryStats (
    val Country: String,
    val CountryCode: String,
    val Province: String,
    val City: String,
    val CityCode: String, // or int?
    val Lat: String,
    val Lon: String,
    val Confirmed: Int,
    val Deaths: Int,
    val Recovered: Int,
    val Active: Int,
    val Date: String
)