package com.example.covid_19.ui.news

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.covid_19.Headline
import com.example.covid_19.R

class NewsAdapter (
    val context: Context,
    val headlines: ArrayList<Headline>,
    val clickListener: (Headline) -> Unit): RecyclerView.Adapter<NewsViewHolder>(), Filterable {

    private var selectedIndex = RecyclerView.NO_POSITION
    private val headlinesAll = ArrayList<Headline>(headlines)

    override fun getItemCount(): Int = headlines.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.news_item, parent, false)
        val holder = NewsViewHolder(view)

        //News article click listener
        view.setOnClickListener {
            clickListener(headlines[holder.adapterPosition])
            val oldSelectedIndex = selectedIndex
            selectedIndex = holder.adapterPosition
            notifyItemChanged(selectedIndex)
            notifyItemChanged(oldSelectedIndex)
        }

        //Share button functionality
        val shareButton = view.findViewById<Button>(R.id.shareButton)
        shareButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "plain/text"
            val subject = "Check out this COVID-19 news article"
            val body: String = headlines[holder.adapterPosition].url + ""
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, body)
            context.startActivity(Intent.createChooser(intent, "Share Using"))
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

    override fun getFilter(): Filter {
        return myFilter
    }

    var myFilter: Filter = object : Filter() {
        //Automatic on background thread
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val filteredList: ArrayList<Headline> = ArrayList()
            if (charSequence.toString().isEmpty()) {
                filteredList.addAll(headlinesAll)
            } else {
                for (headline in headlinesAll) {
                    if (headline.title.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(headline)
                    }
                }
            }
            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        //Automatic on UI thread
        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            headlines.clear()
            headlines.addAll(filterResults.values as Collection<Headline>)
            notifyDataSetChanged()
        }
    }
}
