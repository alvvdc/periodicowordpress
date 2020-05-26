package com.iesvirgendelcarmen.periodicowordpress.view

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.appodeal.ads.NativeAd
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.iesvirgendelcarmen.periodicowordpress.*
import com.iesvirgendelcarmen.periodicowordpress.config.Endpoint
import com.iesvirgendelcarmen.periodicowordpress.model.Resource
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.MenuCategory
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostBO
import com.iesvirgendelcarmen.periodicowordpress.model.room.Bookmark
import com.iesvirgendelcarmen.periodicowordpress.viewmodel.BookmarkViewModel
import com.iesvirgendelcarmen.periodicowordpress.viewmodel.PaginationStatus
import com.iesvirgendelcarmen.periodicowordpress.viewmodel.PaginationViewModel
import com.iesvirgendelcarmen.periodicowordpress.viewmodel.PostBoViewModel


var adPosition = 0

class PostsListFragment :   Fragment(),
                            SwipeRefreshLayout.OnRefreshListener,
                            BottomNavigationView.OnNavigationItemSelectedListener,
                            BottomNavigationView.OnNavigationItemReselectedListener,
                            SetCategoryListener,
                            CategoryLoadListener,
                            BookmarkChangeListener,
                            BackPress {



    private var status = MainActivity.LOAD_HOME
    private var categories = emptyList<MenuCategory>()
    private var adsList = mutableListOf<List<NativeAd>>()

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var thereAreNoBookmarksView: ConstraintLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var postsListRecyclerViewAdapter: PostsListRecyclerViewAdapter

    private lateinit var postsListRecyclerViewOnScrollListener: PostsListRecyclerViewOnScrollListener
    private lateinit var postListListener: PostListListener
    private lateinit var sharePostListener: SharePostListener
    private lateinit var bookmarkPostListener: BookmarkPostListener
    private lateinit var bottomNavigationListener: BottomNavigationListener
    private lateinit var drawerLayoutLock: DrawerLayoutLock
    private lateinit var paginationStatus: PaginationStatus
    private lateinit var appodealCache: AppodealCache

    private val postViewModel by lazy {
        ViewModelProvider(this).get(PostBoViewModel::class.java)
    }

    private val bookmarkViewModel by lazy {
        ViewModelProvider(this).get(BookmarkViewModel::class.java)
    }

    private val paginationViewModel by lazy {
        ViewModelProvider(this).get(PaginationViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        postListListener = context as PostListListener
        sharePostListener = context as SharePostListener
        bookmarkPostListener = context as BookmarkPostListener
        bottomNavigationListener = context as BottomNavigationListener
        drawerLayoutLock = context as DrawerLayoutLock
        appodealCache = context as AppodealCache
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.status = arguments?.getInt(MainActivity.STATUS_KEY, MainActivity.LOAD_HOME)!!
        paginationStatus = paginationViewModel.paginationStatus
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (isBookmarkLoadEnabled())
            drawerLayoutLock.lockDrawerLayout()

        return inflater.inflate(R.layout.fragment_posts_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setRecyclerView(view)


        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener(this)

        bottomNavigation = view.findViewById(R.id.bottomNavigation)
        bottomNavigation.setOnNavigationItemSelectedListener(this)
        bottomNavigation.setOnNavigationItemReselectedListener(this)

        thereAreNoBookmarksView = view.findViewById(R.id.noBookmarksView)
        thereAreNoBookmarksView.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        observePostsListLiveData()
        getPostsOnStart()
    }

    override fun onDestroyView() {
        if (isBookmarkLoadEnabled() || isTrendingLoadEnabled())
            drawerLayoutLock.unlockDrawerLayout()

        super.onDestroyView()
    }

    //

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

    override fun onNavigationItemReselected(item: MenuItem) {
        if (item.itemId == R.id.home || item.itemId == R.id.bookmark) {
            recyclerView.smoothScrollToPosition(0)
        }
    }

    override fun onRefresh() {
        postsListRecyclerViewAdapter.postsList = mutableListOf()

        paginationStatus.reset()

        if (isBookmarkLoadEnabled()) {
            bookmarkViewModel.getAll().observe(viewLifecycleOwner, Observer {
                postViewModel.getPosts(paginationStatus.page, paginationStatus.category, Bookmark.convertListToArray(it))
            })
        } else {
            postViewModel.getPosts(paginationStatus.page, paginationStatus.category)
        }
    }

    //

    private fun setRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        postsListRecyclerViewAdapter = PostsListRecyclerViewAdapter(postListListener, sharePostListener, bookmarkPostListener)
        postsListRecyclerViewAdapter.menuCategoriesList = categories
        postsListRecyclerViewAdapter.appodealCache = appodealCache
        val linearLayoutManager = NpaLinearLayoutManager(context)

        recyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = postsListRecyclerViewAdapter
        }

        postsListRecyclerViewOnScrollListener = PostsListRecyclerViewOnScrollListener(onLoadMoreListener())
        recyclerView.addOnScrollListener(postsListRecyclerViewOnScrollListener)
        recyclerView.addItemDecoration(MemberItemDecoration())
    }

    private fun onLoadMoreListener(): PostsListRecyclerViewOnScrollListener.LoadMoreListener {
        return object : PostsListRecyclerViewOnScrollListener.LoadMoreListener {
            override fun onLoadMore() {
                if (!paginationStatus.isListEnded) {
                    paginationStatus.nextPage()
                    swipeRefresh.isRefreshing = true
                    paginationStatus.isLoading = true
                    postViewModel.getPosts(paginationStatus.page, paginationStatus.category)
                }
            }
        }
    }

    private fun observePostsListLiveData() {
        postViewModel.postsBoListLiveData.observe(viewLifecycleOwner, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    onSuccessPostsObserver(resource.data)
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(context, getString(R.string.LOADING_POSTS_ERROR), Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun onSuccessPostsObserver(posts: List<PostBO>) {

        if (!isBookmarkLoadEnabled() && !postsListRecyclerViewAdapter.postsList.containsAll(posts)) {
            postsListRecyclerViewAdapter.postsList.addAll(posts)
            postsListRecyclerViewAdapter.notifyItemRangeChanged((paginationStatus.page - 1) * 10, Endpoint.DEFAULT_PER_PAGE)
        } else if (isBookmarkLoadEnabled() && swipeRefresh.isRefreshing) {
            postsListRecyclerViewAdapter.postsList = posts.toMutableList()
            postsListRecyclerViewAdapter.notifyDataSetChanged()
        }

        if (posts.size < Endpoint.DEFAULT_PER_PAGE)
            paginationStatus.isListEnded = true
        else if (adsList.size > 0 && adPosition < adsList.size) {
            postsListRecyclerViewAdapter.postsList.add(adsList[adPosition])
            adPosition++
        }
        else if (adPosition >= adsList.size) {
            if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                adsList.add(emptyList())
            }
        }

        swipeRefresh.isRefreshing = false
        paginationStatus.isLoading = false
    }

    private fun getPostsOnStart() {
        if (paginationStatus.page > 0)
            return

        swipeRefresh.isRefreshing = true
        paginationStatus.page = 1

        if (isBookmarkLoadEnabled()) {
            bottomNavigation.checkItem(R.id.bookmark)

            val bookmarkLiveData = bookmarkViewModel.getAll()

            bookmarkLiveData.observe(viewLifecycleOwner, Observer {
                if (it.isEmpty()) {
                    thereAreNoBookmarksView.visibility = View.VISIBLE
                    swipeRefresh.isRefreshing = false
                } else {
                    postViewModel.getPosts(paginationStatus.page, paginationStatus.category, Bookmark.convertListToArray(it))
                }
            })

        } else if (isTrendingLoadEnabled()) {
            bottomNavigation.checkItem(R.id.trending)
            postViewModel.getPosts(popularPosts = true)
        } else {
            postViewModel.getPosts()
        }
    }

    private fun isBookmarkLoadEnabled() = status == MainActivity.LOAD_BOOKMARKS

    private fun isTrendingLoadEnabled() = status == MainActivity.LOAD_TRENDING

    private fun BottomNavigationView.checkItem(actionId: Int) {
        menu.findItem(actionId)?.isChecked = true
    }

    //

    override fun onSetCategory(categoryId: Int) {
        postsListRecyclerViewAdapter.postsList = mutableListOf()
        swipeRefresh.isRefreshing = true

        paginationStatus.reset()
        paginationStatus.category = categoryId
        postViewModel.getPosts(paginationStatus.page, paginationStatus.category)
    }

    override fun onCategoriesLoaded(categories: List<MenuCategory>) {
        this.categories = categories

        if (::postsListRecyclerViewAdapter.isInitialized)
            postsListRecyclerViewAdapter.menuCategoriesList = categories
    }

    override fun onPostBookmarkedChange(post: PostBO) {
        val index = postsListRecyclerViewAdapter.postsList.indexOf(post)
        postsListRecyclerViewAdapter.notifyItemChanged(index)
    }

    override fun onBackPressed() {
        if (!isBookmarkLoadEnabled())
            bottomNavigation.checkItem(R.id.home)
    }
}

//

interface SetCategoryListener {
    fun onSetCategory(categoryId: Int)
}

interface CategoryLoadListener {
    fun onCategoriesLoaded(categories: List<MenuCategory>)
}

interface BookmarkChangeListener {
    fun onPostBookmarkedChange(post: PostBO)
}

interface BottomNavigationListener {
    fun onBottomNavigationHomeSelected()
    fun onBottomNavigationTrendingSelected()
    fun onBottomNavigationBookmarkSelected()
}

interface BackPress {
    fun onBackPressed()
}

//

private class PostsListRecyclerViewOnScrollListener(private val callback: LoadMoreListener): RecyclerView.OnScrollListener() {

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

private class MemberItemDecoration: RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
            outRect.bottom = 170
        }
    }
}

private class NpaLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {
    override fun supportsPredictiveItemAnimations() = false
}