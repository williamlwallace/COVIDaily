package com.example.covid_19.ui.news

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.covid_19.R

class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val headlineText: TextView = view.findViewById(R.id.headlineText)
}
