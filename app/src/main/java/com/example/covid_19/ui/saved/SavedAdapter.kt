package com.example.covid_19.ui.saved

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.covid_19.AppDatabase
import com.example.covid_19.Headline
import com.example.covid_19.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SavedAdapter internal constructor(
    val context: Context
): RecyclerView.Adapter<SavedAdapter.SavedViewHolder>(), Filterable {

    private var headlines = emptyList<Headline>() // Cached copy of words
    private var headlinesAll = ArrayList<Headline>(headlines)
    private var selectedIndex = RecyclerView.NO_POSITION


    inner class SavedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headlineText: TextView = itemView.findViewById(R.id.headlineText)
        val headlineImage: ImageView = itemView.findViewById(R.id.newsImage)
        var isActive: Boolean = false
            set(value) {
                field = value
                itemView.setBackgroundColor(if (field) Color.parseColor("#4D000000") else Color.TRANSPARENT)
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.news_item, parent, false)
        val holder = SavedViewHolder(view)

        //News article click listener
        view.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse((headlines[holder.adapterPosition]).url))
            context.startActivity(intent)
        }

        //Unsave news article
        val saveButton = view.findViewById<ImageButton>(R.id.saveButton)
        saveButton.setOnClickListener {
            GlobalScope.launch {
                AppDatabase.getDatabase(context, this).headlineDao().delete(headlines[holder.adapterPosition])
            }
            Toast.makeText(context, "Unsaved article!", Toast.LENGTH_SHORT).show()
        }

        //Share button functionality
        val shareButton = view.findViewById<ImageButton>(R.id.shareButton)
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

    override fun onBindViewHolder(holder: SavedViewHolder, i: Int) {
        holder.headlineText.text = headlines[i].title
        Glide
            .with(context)
            .load(headlines[i].image)
            .centerCrop()
            .into(holder.headlineImage);
        holder.isActive = selectedIndex == i
    }

    internal fun setHeadlines(headlines: ArrayList<Headline>) {
        this.headlines = headlines
        notifyDataSetChanged()
    }

    override fun getItemCount() = headlines.size

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
            val headlines : ArrayList<Headline> = headlines as ArrayList<Headline>
            headlines.clear()
            headlines.addAll(filterResults.values as Collection<Headline>)
            notifyDataSetChanged()
        }
    }
}
