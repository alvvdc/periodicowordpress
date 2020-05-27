package com.iesvirgendelcarmen.periodicowordpress

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appodeal.ads.Appodeal
import com.appodeal.ads.NativeAd
import com.appodeal.ads.NativeCallbacks
import com.appodeal.ads.native_ad.views.NativeAdViewContentStream
import com.iesvirgendelcarmen.periodicowordpress.config.CategoryColor
import com.iesvirgendelcarmen.periodicowordpress.model.Resource
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.MediaBO
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.MenuCategory
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.MenuCategoryMapper
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostBO
import com.iesvirgendelcarmen.periodicowordpress.model.room.Bookmark
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Category
import com.iesvirgendelcarmen.periodicowordpress.view.*
import com.iesvirgendelcarmen.periodicowordpress.viewmodel.BookmarkViewModel
import com.iesvirgendelcarmen.periodicowordpress.viewmodel.wordpress.CategoryViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_posts_list.*

class MainActivity :    AppCompatActivity(),
                        MenuCategoryListener,
                        PostListListener,
                        SharePostListener,
                        BookmarkPostListener,
                        ImageDetailListener,
                        CloseFragmentListener,
                        BottomNavigationListener,
                        DrawerLayoutLock,
                        AppVersionRequest,
                        OpenWebPageRequest,
                        AppodealCache,
                        AppodealView {


    private var bookmarks = mutableListOf<Bookmark>()
    private var menuCategoriesList = listOf<MenuCategory>()

    lateinit var postsListFragment: PostsListFragment
    lateinit var categoriesRecyclerView: RecyclerView
    lateinit var categoriesRecyclerViewAdapter: CategoriesRecyclerViewAdapter
    lateinit var postsBookmarkFragment: PostsListFragment

    private lateinit var toolbarRelativeLayout: RelativeLayout

    private val categoryViewModel by lazy {
        ViewModelProvider(this).get(CategoryViewModel::class.java)
    }

    private val bookmarkViewModel by lazy {
        ViewModelProvider(this).get(BookmarkViewModel::class.java)
    }

    companion object {
        const val STATUS_KEY = "STATUS"
        const val LOAD_HOME = 0
        const val LOAD_BOOKMARKS = 1
        const val LOAD_TRENDING = 2

        const val MAIN_CATEGORY_ID = -1

        const val IMAGE_DETAIL_FRAGMENT_TAG = "IMAGE_DETAIL_FRAGMENT"

        const val COLOR_KEY = "COLOR"
        const val POST_KEY = "POST"

        const val APPODEAL_KEY = "932102fd93a91d111426d3ad1ecefb0aadcbf12bd2478367"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        setDrawerLayout()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbarRelativeLayout = findViewById(R.id.toolbarRelativeLayout)

        addCategoriesToNavigationDrawer()

        startPostListFragment(savedInstanceState)

        setCategoriesRecyclerView()

        val infoImageView = findViewById<ImageView>(R.id.info)
        infoImageView.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)

            supportFragmentManager.beginTransaction()
                .add(R.id.container, InfoFragment())
                .addToBackStack(null)
                .commit()
        }


        Appodeal.disableLocationPermissionCheck()
        Appodeal.setTesting(true)

        Appodeal.setBannerViewId(R.id.banner_ad_view)

        Appodeal.initialize(this, APPODEAL_KEY, Appodeal.NATIVE or Appodeal.BANNER, false)
        Appodeal.cache(this, Appodeal.NATIVE)
    }

    override fun onStart() {
        super.onStart()

        bookmarkViewModel.getAll().observe(this, Observer {
            bookmarks = it.toMutableList()
        })
    }

    //

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val imageDetailFragment = supportFragmentManager.findFragmentByTag(IMAGE_DETAIL_FRAGMENT_TAG)

        if (imageDetailFragment == null)
            showActionBar()

        if (!::postsBookmarkFragment.isInitialized || postsBookmarkFragment == null || !postsBookmarkFragment.isVisible)
            unlockDrawerLayout()

        postsListFragment.onBackPressed()

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    //

    private fun setCategoriesRecyclerView() {
        categoriesRecyclerView = findViewById(R.id.recyclerView)
        categoriesRecyclerView.layoutManager = LinearLayoutManager(this)

        categoriesRecyclerViewAdapter = CategoriesRecyclerViewAdapter(emptyList(), this)
        categoriesRecyclerView.adapter = categoriesRecyclerViewAdapter
    }

    private fun setDrawerLayout() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }

    private fun addCategoriesToNavigationDrawer() {
        categoryViewModel.categoryListLiveData.observe(this, Observer { resource ->

            when (resource.status) {
                Resource.Status.SUCCESS -> {

                    val menuCategories = mutableListOf<MenuCategory>()
                    val mainCategory = MenuCategory(MAIN_CATEGORY_ID, getString(R.string.homeCategory), resources.getColor(R.color.colorPrimary))
                    menuCategories.add(mainCategory)

                    for (category in resource.data) {
                        val menuCategory = MenuCategoryMapper.convertCategoryToMenuCategory(category)
                        menuCategories.add(menuCategory)
                    }

                    menuCategoriesList = menuCategories
                    postsListFragment.onCategoriesLoaded(menuCategories)
                    updateCategoriesRecyclerViewAdapter()
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, getString(R.string.loadingCategoriesError), Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {

                }
            }
        })

        categoryViewModel.getCategoriesForNavigationDrawer()
    }

    private fun updateCategoriesRecyclerViewAdapter() {
        categoriesRecyclerViewAdapter.categories = menuCategoriesList
        categoriesRecyclerViewAdapter.notifyDataSetChanged()
    }

    private fun getColorForCategories(categoryList: List<Category>): Int {
        for (category in menuCategoriesList) {
            if (categoryList.isNotEmpty() && category.id == categoryList[0].id) {
                return category.color
            }
        }
        return CategoryColor.DEFAULT_COLOR
    }

    private fun startPostListFragment(savedInstanceState: Bundle?) {
        val bundle = Bundle()
        bundle.putInt(STATUS_KEY, LOAD_HOME)

        postsListFragment = PostsListFragment()
        postsListFragment.arguments = bundle

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(
                R.id.container,
                postsListFragment
            ).commit()
        }
    }

    private fun startImageDetailFragment(media: MediaBO) {
        val bundle = Bundle()
        bundle.putParcelable("MEDIA", media)

        val imageDetailFragment = ImageDetailFragment()
        imageDetailFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .add(R.id.container, imageDetailFragment, IMAGE_DETAIL_FRAGMENT_TAG)
            .addToBackStack(null)
            .commit()
    }

    private fun startPostDetailFragment(post: PostBO) {
        val bundle = Bundle()
        var color = getColorForCategories(post.categories)

        bundle.putInt(COLOR_KEY, color)
        bundle.putParcelable(POST_KEY, post)

        val postDetailFragment = PostDetailFragment()
        postDetailFragment.arguments = bundle

        hideActionBar()
        supportFragmentManager.beginTransaction().add(
            R.id.container,
            postDetailFragment
        ).addToBackStack(null).commit()

        Appodeal.show(this, Appodeal.BANNER_VIEW)
    }

    private fun startBookmarkListFragment() {
        val bundle = Bundle()
        bundle.putInt(STATUS_KEY, LOAD_BOOKMARKS)

        postsBookmarkFragment = PostsListFragment()
        postsBookmarkFragment.arguments = bundle
        postsBookmarkFragment.onCategoriesLoaded(menuCategoriesList)

        supportFragmentManager.beginTransaction()
            .add(R.id.container, postsBookmarkFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun startTrendingListFragment() {
        val bundle = Bundle()
        bundle.putInt(STATUS_KEY, LOAD_TRENDING)

        postsBookmarkFragment = PostsListFragment()
        postsBookmarkFragment.arguments = bundle
        postsBookmarkFragment.onCategoriesLoaded(menuCategoriesList)

        supportFragmentManager.beginTransaction()
            .add(R.id.container, postsBookmarkFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showActionBar() {
        supportActionBar?.show()
        toolbarRelativeLayout.visibility = View.VISIBLE
    }

    private fun hideActionBar() {
        supportActionBar?.hide()
        toolbarRelativeLayout.visibility = View.GONE
    }

    private fun getAppVersionCode(): Int {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        return packageInfo.versionCode
    }

    private fun getAppVersionName(): String {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        return packageInfo.versionName
    }

    private fun openWebPage(url: String) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    //

    override fun onClickMenuCategory(category: MenuCategory) {
        postsListFragment.onSetCategory(category.id)
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onClickPost(post: PostBO) {
        startPostDetailFragment(post)
    }

    override fun onClickSharePost(post: PostBO) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "${post.title.rendered}: \n${post.link}")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    override fun onBookmarkPost(post: PostBO): Boolean {
        val bookmark = Bookmark(post.id)

        if (bookmarks.contains(bookmark)) {
            bookmarkViewModel.remove(bookmark)

            if (bookmarks.contains(bookmark)) bookmarks.remove(bookmark)
            postsListFragment.onPostBookmarkedChange(post)

            if (::postsBookmarkFragment.isInitialized)
                postsBookmarkFragment.onPostBookmarkedChange(post)
            return false
        } else {
            bookmarkViewModel.add(bookmark)
            
            if (!bookmarks.contains(bookmark)) bookmarks.add(bookmark)
            postsListFragment.onPostBookmarkedChange(post)

            if (::postsBookmarkFragment.isInitialized)
                postsBookmarkFragment.onPostBookmarkedChange(post)
            return true
        }
        return false
    }

    override fun isPostBookmarked(post: PostBO) = bookmarks.contains(Bookmark(post.id))

    override fun onImageClickListener(media: MediaBO) {
        startImageDetailFragment(media)
    }

    override fun onClickCloseFragment() {
        supportFragmentManager.popBackStack()
    }

    override fun onBottomNavigationHomeSelected() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, postsListFragment)
            .commit()

        this.postsListFragment.bottomNavigation.menu.findItem(R.id.home).isChecked = true
    }

    override fun onBottomNavigationTrendingSelected() {
        startTrendingListFragment()
    }

    override fun onBottomNavigationBookmarkSelected() {
        startBookmarkListFragment()
    }

    override fun lockDrawerLayout() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun unlockDrawerLayout() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun getVersionCode() = getAppVersionCode()

    override fun getVersionName() = getAppVersionName()

    override fun openWebPageFromRequest(url: String)= openWebPage(url)

    override fun appodealCacheRequest() = Appodeal.cache(this, Appodeal.NATIVE)

    override fun showAppodealBannerView() = Appodeal.show(this, Appodeal.BANNER_VIEW)
}

interface SharePostListener {
    fun onClickSharePost(post: PostBO)
}

interface BookmarkPostListener {
    fun onBookmarkPost(post: PostBO): Boolean
    fun isPostBookmarked(post: PostBO): Boolean
}

interface CloseFragmentListener {
    fun onClickCloseFragment()
}

interface DrawerLayoutLock {
    fun lockDrawerLayout()
    fun unlockDrawerLayout()
}

interface AppVersionRequest {
    fun getVersionCode(): Int
    fun getVersionName(): String
}

interface OpenWebPageRequest {
    fun openWebPageFromRequest(url: String)
}

interface AppodealCache {
    fun appodealCacheRequest()
}

interface AppodealView {
    fun showAppodealBannerView(): Boolean
}