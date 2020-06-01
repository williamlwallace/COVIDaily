package com.example.covid_19.ui.news

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.covid_19.R
import org.w3c.dom.Text

class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val headlineText: TextView = view.findViewById(R.id.headlineText)
    val headlineImage: ImageView = view.findViewById(R.id.newsImage)

    var isActive: Boolean = false
        set(value) {
            field = value
            itemView.setBackgroundColor(if (field) Color.parseColor("#4D000000") else Color.TRANSPARENT)
        }
}
