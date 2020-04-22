package com.iesvirgendelcarmen.periodicowordpress.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.iesvirgendelcarmen.periodicowordpress.R
import com.iesvirgendelcarmen.periodicowordpress.model.Resource
import com.iesvirgendelcarmen.periodicowordpress.viewmodel.PostBoViewModel
import com.iesvirgendelcarmen.periodicowordpress.viewmodel.wordpress.PostViewModel

class PostsListFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(PostBoViewModel::class.java)
    }

    lateinit var postsListRecyclerViewAdapter: PostsListRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_posts_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        postsListRecyclerViewAdapter = PostsListRecyclerViewAdapter(emptyList())

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postsListRecyclerViewAdapter
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.postsBoListLiveData.observe(viewLifecycleOwner, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    postsListRecyclerViewAdapter.postsList = resource.data
                    postsListRecyclerViewAdapter.notifyDataSetChanged()
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(context, getString(R.string.LOADING_POSTS_ERROR), Toast.LENGTH_LONG).show()
                }
            }
        })

        viewModel.getAllPostsBO()
    }
}
