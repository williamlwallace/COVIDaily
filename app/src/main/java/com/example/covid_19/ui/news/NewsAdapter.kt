package com.example.covid_19.ui.news

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.covid_19.Headline
import com.example.covid_19.R

class NewsAdapter(
    val context: Context,
    val headlines: List<Headline>,
    val clickListener: (Headline) -> Unit): RecyclerView.Adapter<NewsViewHolder>() {

    private var selectedIndex = RecyclerView.NO_POSITION

    override fun getItemCount(): Int = headlines.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.news_item, parent, false)
        val holder = NewsViewHolder(view)
        val shareButton: Button = view.findViewById(R.id.shareBtn)


        view.setOnClickListener {
            clickListener(headlines[holder.adapterPosition])
            val oldSelectedIndex = selectedIndex
            selectedIndex = holder.adapterPosition
            notifyItemChanged(selectedIndex)
            notifyItemChanged(oldSelectedIndex)
        }


        return holder
    }

    override fun onBindViewHolder(holder: NewsViewHolder, i: Int) {
        holder.headlineText.text = headlines[i].title
        Glide
            .with(context)
            .load(headlines[i].image)
            .centerCrop()
            .into(holder.headlineImage);
        holder.isActive = selectedIndex == i
    }
}
