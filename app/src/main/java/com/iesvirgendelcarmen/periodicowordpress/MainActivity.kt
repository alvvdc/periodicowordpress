package com.iesvirgendelcarmen.periodicowordpress

import android.content.Intent
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.iesvirgendelcarmen.periodicowordpress.config.CategoryColor
import com.iesvirgendelcarmen.periodicowordpress.model.Resource
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.MediaBO
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.MenuCategory
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.MenuCategoryMapper
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostBO
import com.iesvirgendelcarmen.periodicowordpress.model.room.Bookmark
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Media
import com.iesvirgendelcarmen.periodicowordpress.view.*
import com.iesvirgendelcarmen.periodicowordpress.viewmodel.BookmarkViewModel
import com.iesvirgendelcarmen.periodicowordpress.viewmodel.wordpress.CategoryViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MenuCategoryListener, PostListListener, SharePostListener, BookmarkPostListener, ImageDetailListener, CloseFragmentListener {

    private val categoryViewModel by lazy {
        ViewModelProvider(this).get(CategoryViewModel::class.java)
    }

    private val bookmarkViewModel by lazy {
        ViewModelProvider(this).get(BookmarkViewModel::class.java)
    }

    private var bookmarks = mutableListOf<Bookmark>()

    lateinit var postsListFragment: PostsListFragment
    lateinit var categoriesRecyclerView: RecyclerView
    lateinit var categoriesRecyclerViewAdapter: CategoriesRecyclerViewAdapter

    val MAIN_CATEGORY_ID = -1

    lateinit var menuCategoriesList: List<MenuCategory>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        addCategoriesToNavigationDrawer()

        postsListFragment = PostsListFragment()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(
                R.id.container,
                postsListFragment
            ).commit()
        }

        categoriesRecyclerView = findViewById(R.id.recyclerView)
        categoriesRecyclerView.layoutManager = LinearLayoutManager(this)

        categoriesRecyclerViewAdapter = CategoriesRecyclerViewAdapter(emptyList(), this)
        categoriesRecyclerView.adapter = categoriesRecyclerViewAdapter

        bookmarkViewModel.getAll().observe(this, Observer {
            bookmarks = it.toMutableList()
        })
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
                    categoriesRecyclerViewAdapter.categories = menuCategories
                    categoriesRecyclerViewAdapter.notifyDataSetChanged()
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
        supportActionBar?.show()

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    override fun onClickMenuCategory(category: MenuCategory) {
        postsListFragment.onSetCategory(category.id)
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onClickPost(post: PostBO) {
        val bundle = Bundle()
        var color = CategoryColor.DEFAULT_COLOR

        for (category in menuCategoriesList) {
            if (post.categories.isNotEmpty() && category.id == post.categories[0].id) {
                color = category.color
                break
            }
        }

        bundle.putInt("COLOR", color)
        bundle.putParcelable("POST", post)

        val postDetailFragment = PostDetailFragment()
        postDetailFragment.arguments = bundle

        supportActionBar?.hide()
        supportFragmentManager.beginTransaction().add(
            R.id.container,
            postDetailFragment
        ).addToBackStack(null).commit()
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
            postsListFragment.onNotifyListForBookmark(post)
            return false
        } else {
            bookmarkViewModel.add(bookmark)
            
            if (!bookmarks.contains(bookmark)) bookmarks.add(bookmark)
            postsListFragment.onNotifyListForBookmark(post)
            return true
        }
    }

    override fun isPostBookmarked(post: PostBO) = bookmarks.contains(Bookmark(post.id))

    override fun onImageClickListener(media: MediaBO) {
        val bundle = Bundle()
        bundle.putParcelable("MEDIA", media)

        val imageDetailFragment = ImageDetailFragment()
        imageDetailFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .add(R.id.container, imageDetailFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onClickCloseFragment() {
        supportFragmentManager.popBackStack()
    }
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