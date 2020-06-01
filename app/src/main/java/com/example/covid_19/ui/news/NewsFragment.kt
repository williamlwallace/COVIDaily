package com.example.covid_19.ui.news

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.covid_19.Headline
import com.example.covid_19.R
import com.example.covid_19.SharedPreference
import org.json.JSONObject
import java.io.BufferedInputStream
import java.lang.ref.WeakReference
import java.net.URL
import java.nio.charset.Charset
import javax.net.ssl.HttpsURLConnection

class NewsFragment : Fragment() {

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsPicker : RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    lateinit var sharedPreference: SharedPreference
    private var KEY = "a10bc7fd2caf45058eef8547fb8e7b74"
    private var searchView: SearchView? = null
    private var queryTextListener: SearchView.OnQueryTextListener? = null

    var headlines: ArrayList<Headline> = arrayListOf()
        set(value) {
            field = value
            newsAdapter = NewsAdapter(this.requireContext(), field) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.url))
                startActivity(intent)
            }
            newsPicker.adapter = newsAdapter
        }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        newsViewModel =
                ViewModelProviders.of(this).get(NewsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_news, container, false)
        return root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference = SharedPreference(activity!!.applicationContext)
        val countryISO = sharedPreference.getValueString("COUNTRY_ISO")
        val parameters = mapOf("q" to "covid", "apiKey" to KEY, "country" to countryISO!!)
        val url = parameterizeUrl("https://newsapi.org/v2/top-headlines", parameters)
        HeadlinesDownloader(this).execute(url)

        newsPicker = view!!.findViewById<RecyclerView>(R.id.headlinesPicker)
        val layoutManager = LinearLayoutManager(this.requireContext())
        newsPicker.layoutManager = layoutManager
        setHasOptionsMenu(true);
    }

    //Search functionality
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchManager: SearchManager =
            activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }
        if (searchView != null) {
            searchView!!.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))
            queryTextListener = object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    newsAdapter.filter.filter(newText)
                    return true
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    return true
                }
            }
            searchView!!.setOnQueryTextListener(queryTextListener)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }


    @SuppressLint("StaticFieldLeak")
    inner class HeadlinesDownloader(val activity: NewsFragment) : AsyncTask<URL, Void, List<Headline>>() {
        private val context = WeakReference(activity)

        override fun doInBackground(vararg urls: URL): List<Headline> {
            val result = getJson(urls[0])

            val headlinesJson = result.getJSONArray("articles")
            val headlines = (0 until headlinesJson.length()).map { i ->
                val headline = headlinesJson.getJSONObject(i)
                Headline(
                    headline.getString("title"),
                    headline.getString("urlToImage"),
                    headline.getString("publishedAt"),
                    headline.getString("url")
                )
            }
            return headlines
        }

        override fun onPostExecute(headlines: List<Headline>) {
            super.onPostExecute(headlines)
            context.get()?.headlines = headlines as ArrayList<Headline>
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun parameterizeUrl(url: String, parameters: Map<String, String>): URL {
        val builder = Uri.parse(url).buildUpon()
        parameters.forEach { key, value -> builder.appendQueryParameter(key, value) }
        val uri = builder.build()
        return URL(uri.toString())
    }

    private fun getJson(url: URL): JSONObject {
        val connection = url.openConnection() as HttpsURLConnection
        try {
            val json = BufferedInputStream(connection.inputStream).readBytes().toString(Charset.defaultCharset())
            return JSONObject(json)
        } finally {
            connection.disconnect()
        }
    }

}
