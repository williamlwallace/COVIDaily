package com.example.covid_19.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.covid_19.CountryStats
import com.example.covid_19.R
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.*
import java.io.IOException

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private val client = OkHttpClient()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        get("https://api.covid19api.com/total/country/new-zealand")

        return root
    }

    fun get(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response.body()?.string()
                    val gson = GsonBuilder().create()
                    val stats: List<CountryStats> = gson.fromJson(body, Array<CountryStats>::class.java).toList()
                    homeConfirmedCount.text = stats.last().Confirmed.toString()
                    homeRecoveredCount.text = stats.last().Recovered.toString()
                    homeDeathsCount.text = stats.last().Deaths.toString()
                }
            }
        })
    }
}
