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
import com.iesvirgendelcarmen.periodicowordpress.config.Endpoint
import com.iesvirgendelcarmen.periodicowordpress.model.Resource
import com.iesvirgendelcarmen.periodicowordpress.viewmodel.PostBoViewModel
import com.iesvirgendelcarmen.periodicowordpress.viewmodel.wordpress.PostViewModel

class PostsListFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(PostBoViewModel::class.java)
    }

    lateinit var postsListRecyclerViewAdapter: PostsListRecyclerViewAdapter
    lateinit var postsListRecyclerViewOnScrollListener: PostsListRecyclerViewOnScrollListener

    var page = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_posts_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        postsListRecyclerViewAdapter = PostsListRecyclerViewAdapter()
        val linearLayoutManager = LinearLayoutManager(context)

        recyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = postsListRecyclerViewAdapter
        }

        postsListRecyclerViewOnScrollListener = PostsListRecyclerViewOnScrollListener(object: PostsListRecyclerViewOnScrollListener.LoadMoreListener {
            override fun onLoadMore() {
                page++
                viewModel.getPosts(page)
            }
        })
        recyclerView.addOnScrollListener(postsListRecyclerViewOnScrollListener)
    }

    override fun onStart() {
        super.onStart()

        viewModel.postsBoListLiveData.observe(viewLifecycleOwner, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    postsListRecyclerViewAdapter.postsList.addAll(resource.data)

                    val insertIndex = (page - 1) * 10
                    postsListRecyclerViewAdapter.notifyItemRangeInserted(insertIndex, Endpoint.DEFAULT_PER_PAGE)

                    if (postsListRecyclerViewOnScrollListener.isLoading)
                        postsListRecyclerViewOnScrollListener.isLoading = false
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(context, getString(R.string.LOADING_POSTS_ERROR), Toast.LENGTH_LONG).show()
                }
            }
        })

        viewModel.getPosts()
    }
}

class PostsListRecyclerViewOnScrollListener(private val callback: LoadMoreListener): RecyclerView.OnScrollListener() {

    private var visibleThreshold = Endpoint.DEFAULT_PER_PAGE
    private var totalItemCount = 0
    private var lastVisibleItem = 0
    var isLoading = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val linearLayoutManager = (recyclerView.layoutManager as LinearLayoutManager)

        if (dy <= 0) return

        totalItemCount = linearLayoutManager.itemCount
        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()


        if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
            callback.onLoadMore()
            isLoading = true
        }
    }

    interface LoadMoreListener {
        fun onLoadMore()
    }
}