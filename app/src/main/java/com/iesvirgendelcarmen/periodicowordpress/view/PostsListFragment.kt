package com.iesvirgendelcarmen.periodicowordpress.view

import android.content.Context
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.iesvirgendelcarmen.periodicowordpress.MainActivity

import com.iesvirgendelcarmen.periodicowordpress.R
import com.iesvirgendelcarmen.periodicowordpress.SharePostListener
import com.iesvirgendelcarmen.periodicowordpress.config.Endpoint
import com.iesvirgendelcarmen.periodicowordpress.model.Resource
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostBO
import com.iesvirgendelcarmen.periodicowordpress.viewmodel.PostBoViewModel
import com.iesvirgendelcarmen.periodicowordpress.viewmodel.wordpress.PostViewModel

class PostsListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, SetCategoryListener {

    private val viewModel by lazy {
        ViewModelProvider(this).get(PostBoViewModel::class.java)
    }

    lateinit var swipeRefresh: SwipeRefreshLayout

    private lateinit var postsListRecyclerViewAdapter: PostsListRecyclerViewAdapter
    private lateinit var postsListRecyclerViewOnScrollListener: PostsListRecyclerViewOnScrollListener
    private lateinit var postListListener: PostListListener
    private lateinit var sharePostListener: SharePostListener

    val paginationStatus = PaginationStatus()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_posts_list, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        postListListener = context as PostListListener
        sharePostListener = context as SharePostListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        postsListRecyclerViewAdapter = PostsListRecyclerViewAdapter(postListListener, sharePostListener)
        val linearLayoutManager = NpaLinearLayoutManager(context)

        recyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = postsListRecyclerViewAdapter
        }

        postsListRecyclerViewOnScrollListener = PostsListRecyclerViewOnScrollListener(object: PostsListRecyclerViewOnScrollListener.LoadMoreListener {
            override fun onLoadMore() {
                if (!paginationStatus.isListEnded) {
                    paginationStatus.nextPage()
                    swipeRefresh.isRefreshing = true
                    paginationStatus.isLoading = true
                    viewModel.getPosts(paginationStatus.page, paginationStatus.category)
                }
            }
        })
        recyclerView.addOnScrollListener(postsListRecyclerViewOnScrollListener)

        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener(this)
    }

    override fun onStart() {
        super.onStart()

        viewModel.postsBoListLiveData.observe(viewLifecycleOwner, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    postsListRecyclerViewAdapter.postsList.addAll(resource.data)
                    postsListRecyclerViewAdapter.notifyItemRangeChanged((paginationStatus.page - 1) * 10, Endpoint.DEFAULT_PER_PAGE)

                    if (resource.data.size < Endpoint.DEFAULT_PER_PAGE)
                        paginationStatus.isListEnded = true

                    swipeRefresh.isRefreshing = false
                    paginationStatus.isLoading = false
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(context, getString(R.string.LOADING_POSTS_ERROR), Toast.LENGTH_LONG).show()
                }
            }
        })

        if (paginationStatus.page <= 1) {
            swipeRefresh.isRefreshing = true
            viewModel.getPosts()
        }
    }

    data class PaginationStatus(var isLoading: Boolean = false, var page: Int = 1, var isListEnded: Boolean = false, var category: Int = -1) {
        fun nextPage() = page++

        fun reset() {
            isLoading = false
            page = 1
            isListEnded = false
            category = -1
        }
    }

    override fun onSetCategory(categoryId: Int) {
        postsListRecyclerViewAdapter.postsList = mutableListOf()
        swipeRefresh.isRefreshing = true

        paginationStatus.reset()
        paginationStatus.category = categoryId
        viewModel.getPosts(paginationStatus.page, paginationStatus.category)
    }

    override fun onRefresh() {
        postsListRecyclerViewAdapter.postsList = mutableListOf()

        paginationStatus.reset()
        viewModel.getPosts(paginationStatus.page, paginationStatus.category)
    }
}

class PostsListRecyclerViewOnScrollListener(private val callback: LoadMoreListener): RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val itemCount = (recyclerView.layoutManager as LinearLayoutManager).itemCount
        val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

        if (lastVisibleItemPosition == itemCount - 1)
            callback.onLoadMore()
    }

    interface LoadMoreListener {
        fun onLoadMore()
    }
}

interface SetCategoryListener {
    fun onSetCategory(categoryId: Int)
}

private class NpaLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {
    override fun supportsPredictiveItemAnimations() = false
}