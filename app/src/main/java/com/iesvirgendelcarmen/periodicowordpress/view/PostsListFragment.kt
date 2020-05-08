package com.iesvirgendelcarmen.periodicowordpress.view

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.iesvirgendelcarmen.periodicowordpress.BookmarkPostListener
import com.iesvirgendelcarmen.periodicowordpress.R
import com.iesvirgendelcarmen.periodicowordpress.SharePostListener
import com.iesvirgendelcarmen.periodicowordpress.config.Endpoint
import com.iesvirgendelcarmen.periodicowordpress.model.Resource
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.MenuCategory
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostBO
import com.iesvirgendelcarmen.periodicowordpress.model.room.Bookmark
import com.iesvirgendelcarmen.periodicowordpress.model.room.BookmarkList
import com.iesvirgendelcarmen.periodicowordpress.viewmodel.PostBoViewModel


class PostsListFragment :   Fragment(),
                            SwipeRefreshLayout.OnRefreshListener,
                            BottomNavigationView.OnNavigationItemSelectedListener,
                            SetCategoryListener,
                            CategoryLoadListener,
                            BookmarkNotifyList,
                            Back {

    private val viewModel by lazy {
        ViewModelProvider(this).get(PostBoViewModel::class.java)
    }

    private var bookmarks = listOf<Bookmark>()
    private var categories = emptyList<MenuCategory>()

    lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var postsListRecyclerViewAdapter: PostsListRecyclerViewAdapter
    private lateinit var postsListRecyclerViewOnScrollListener: PostsListRecyclerViewOnScrollListener
    private lateinit var postListListener: PostListListener
    private lateinit var sharePostListener: SharePostListener
    private lateinit var bookmarkPostListener: BookmarkPostListener
    private lateinit var bottomNavigationListener: BottomNavigationListener

    val paginationStatus = PaginationStatus()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bookmarkList: BookmarkList? = arguments?.getParcelable("BOOKMARKS")
        if (bookmarkList != null) {
            bookmarks = bookmarkList.bookmarkList
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_posts_list, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        postListListener = context as PostListListener
        sharePostListener = context as SharePostListener
        bookmarkPostListener = context as BookmarkPostListener
        bottomNavigationListener = context as BottomNavigationListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        postsListRecyclerViewAdapter = PostsListRecyclerViewAdapter(postListListener, sharePostListener, bookmarkPostListener)
        postsListRecyclerViewAdapter.menuCategoriesList = categories
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

        recyclerView.addItemDecoration(MemberItemDecoration())

        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener(this)

        bottomNavigation = view.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnNavigationItemSelectedListener(this)
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
            Log.d("ALVARO", "page <= 1")
            swipeRefresh.isRefreshing = true

            if (bookmarks.isEmpty()) {
                Log.d("ALVARO", "isEmpty")
                viewModel.getPosts()
                bottomNavigation.checkItem(R.id.home)
            }
            else {
                viewModel.getPosts(paginationStatus.page, paginationStatus.category, Bookmark.convertListToArray(bookmarks))
                bottomNavigation.checkItem(R.id.bookmark)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                bottomNavigationListener.onBottomNavigationHomeSelected()
                return true
            }
            R.id.trending -> {
                bottomNavigationListener.onBottomNavigationTrendingSelected()
                return true
            }
            R.id.bookmark -> {
                bottomNavigationListener.onBottomNavigationBookmarkSelected()
                return true
            }
        }
        return false
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
        if (bookmarks.isEmpty()) {
            postsListRecyclerViewAdapter.postsList = mutableListOf()

            paginationStatus.reset()
            viewModel.getPosts(paginationStatus.page, paginationStatus.category)
            bottomNavigation.checkItem(R.id.home)
        } else {
            paginationStatus.reset()
            viewModel.getPosts(paginationStatus.page, paginationStatus.category, Bookmark.convertListToArray(bookmarks))
            bottomNavigation.checkItem(R.id.bookmark)
        }
    }

    override fun onCategoriesLoaded(categories: List<MenuCategory>) {
        this.categories = categories

        if (::postsListRecyclerViewAdapter.isInitialized)
            postsListRecyclerViewAdapter.menuCategoriesList = categories
    }

    override fun onNotifyListForBookmark(post: PostBO) {
        val index = postsListRecyclerViewAdapter.postsList.indexOf(post)
        postsListRecyclerViewAdapter.notifyItemChanged(index)
    }

    private fun BottomNavigationView.checkItem(actionId: Int) {
        menu.findItem(actionId)?.isChecked = true
    }

    override fun onBackPressed() {
        if (bookmarks.isEmpty())
            bottomNavigation.checkItem(R.id.home)
        //else
            //bottomNavigation.checkItem(R.id.bookmark)
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

class MemberItemDecoration: RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
            outRect.bottom = 170
        }
    }
}

interface SetCategoryListener {
    fun onSetCategory(categoryId: Int)
}

interface CategoryLoadListener {
    fun onCategoriesLoaded(categories: List<MenuCategory>)
}

interface BookmarkNotifyList {
    fun onNotifyListForBookmark(post: PostBO)
}

interface BottomNavigationListener {
    fun onBottomNavigationHomeSelected()
    fun onBottomNavigationTrendingSelected()
    fun onBottomNavigationBookmarkSelected()
}

interface Back {
    fun onBackPressed()
}

private class NpaLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {
    override fun supportsPredictiveItemAnimations() = false
}