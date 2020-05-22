package com.example.covid_19.ui.news

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.covid_19.Headline
import com.example.covid_19.R

class NewsAdapter(
    val context: Context,
    val headlines: List<Headline>): RecyclerView.Adapter<NewsViewHolder>() {
    override fun getItemCount(): Int = headlines.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.news_item, parent, false)
        val holder = NewsViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: NewsViewHolder, i: Int) {
        holder.headlineText.text = headlines[i].text
    }
}
