package com.example.covid_19.ui.news

import android.annotation.SuppressLint
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.covid_19.Headline
import com.example.covid_19.R
import kotlinx.android.synthetic.main.fragment_news.*
import org.json.JSONObject
import java.io.BufferedInputStream
import java.lang.ref.WeakReference
import java.net.URL
import java.nio.charset.Charset
import javax.net.ssl.HttpsURLConnection

class NewsFragment : Fragment() {

    private lateinit var newsViewModel: NewsViewModel
    private var KEY = "a10bc7fd2caf45058eef8547fb8e7b74"
    private lateinit var sourcesPicker: Spinner
    private var newsPicker : RecyclerView = headlinesPicker

    var headlines: List<Headline> = listOf()
        set(value) {
            field = value
            newsPicker.adapter = NewsAdapter(this, field)
        }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        newsViewModel =
                ViewModelProviders.of(this).get(NewsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_news, container, false)
//        val textView: TextView = root.findViewById(R.id.text_news)
//        newsViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        val layoutManager = LinearLayoutManager(activity)
        newsPicker.layoutManager = layoutManager






        val parameters = mapOf("q" to "covid", "apiKey" to KEY)
        val url = parameterizeUrl("https://newsapi.org/v2/top-headlines", parameters)
        HeadlinesDownloader(this).execute(url)


        return root
    }


    @SuppressLint("StaticFieldLeak")
    inner class HeadlinesDownloader(val activity: NewsFragment) : AsyncTask<URL, Void, List<Headline>>() {
        private val context = WeakReference(activity)

        override fun doInBackground(vararg urls: URL): List<Headline> {
            val result = getJson(urls[0])

            val headlinesJson = result.getJSONArray("articles")
            val headlines = (0 until headlinesJson.length()).map { i ->
                val headline = headlinesJson.getJSONObject(i)
                Headline(headline.getString("title"), headline.getString("url"))
            }
            return headlines
        }

        override fun onPostExecute(headlines: List<Headline>) {
            super.onPostExecute(headlines)
            context.get()?.headlines = headlines
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
