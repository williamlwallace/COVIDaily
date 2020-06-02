package com.example.covid_19.ui.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.covid_19.CountryStats
import com.example.covid_19.R
import com.example.covid_19.SharedPreference
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.*
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private val client = OkHttpClient()
    lateinit var sharedPreference: SharedPreference

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreference = SharedPreference(requireActivity().applicationContext)
        val country = sharedPreference.getValueString("COUNTRY")
        val countryKebab = sharedPreference.getValueString("COUNTRY_KEBAB_CASE")
        val countryFlag = sharedPreference.getValueString("COUNTRY_FLAG")
        get("https://api.covid19api.com/total/country/$countryKebab")
        locationText.text = "$countryFlag $country"
        dateText.text =
            LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
                .toString()

        //animation
        val ttb = AnimationUtils.loadAnimation(context, R.anim.ttb)
        val btt = AnimationUtils.loadAnimation(context, R.anim.btt)
        dateText.startAnimation(ttb)
        tempText.startAnimation(ttb)
        mainContainer.startAnimation(btt)


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
                    setUiText(stats)
                }
            }
        })
    }

    fun setUiText(stats: List<CountryStats>) {
        val todayStats = stats.last()
        val yesterdayStats = stats[stats.lastIndex - 1]
        val newCases = todayStats.Confirmed - yesterdayStats.Confirmed
        val newCasesString: String =
            if (newCases > 1 || newCases == 0) {
                "There are $newCases new cases"
            }  else {
                "There is $newCases new case"
            }
        sharedPreference.save("DAILY_NOTIFICATION", "$newCasesString in the past 24 hours")

        activity?.runOnUiThread(Runnable {
            newCasesText.text = "$newCasesString in "
            homeConfirmedCount.text = todayStats.Confirmed.toString()
            homeRecoveredCount.text = todayStats.Recovered.toString()
            homeDeathsCount.text = todayStats.Deaths.toString()
        })
    }


}
