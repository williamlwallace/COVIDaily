package com.example.covid_19.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.covid_19.Headline
import com.example.covid_19.R
import kotlinx.android.synthetic.main.fragment_saved.*

class SavedFragment : Fragment() {

    private lateinit var savedViewModel: SavedViewModel
    private lateinit var adapter: SavedAdapter
    private var searchView: SearchView? = null
    private var queryTextListener: SearchView.OnQueryTextListener? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        savedViewModel =
                ViewModelProviders.of(this).get(SavedViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_saved, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = headlinesPicker
        adapter = SavedAdapter(this.requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        savedViewModel = ViewModelProvider(this).get(SavedViewModel::class.java)
        savedViewModel.savedHeadlines.observe(viewLifecycleOwner, Observer { headlines ->
            headlines?.let {adapter.setHeadlines(it as ArrayList<Headline>)}
        })
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)
    }

//    //Search functionality
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.toolbar, menu)
//        val searchItem = menu.findItem(R.id.action_search)
//        val searchManager: SearchManager =
//            activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        if (searchItem != null) {
//            searchView = searchItem.actionView as SearchView
//        }
//        if (searchView != null) {
//            searchView!!.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))
//            queryTextListener = object : SearchView.OnQueryTextListener {
//                override fun onQueryTextChange(newText: String): Boolean {
//                    adapter.filter.filter(newText)
//                    return true
//                }
//
//                override fun onQueryTextSubmit(query: String): Boolean {
//                    return true
//                }
//            }
//            searchView!!.setOnQueryTextListener(queryTextListener)
//        }
//        super.onCreateOptionsMenu(menu, inflater)
//    }
}
