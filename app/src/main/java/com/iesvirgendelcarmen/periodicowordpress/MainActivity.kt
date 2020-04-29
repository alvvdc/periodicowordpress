package com.iesvirgendelcarmen.periodicowordpress

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.iesvirgendelcarmen.periodicowordpress.model.Resource
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostBO
import com.iesvirgendelcarmen.periodicowordpress.view.PostDetailFragment
import com.iesvirgendelcarmen.periodicowordpress.view.PostListListener
import com.iesvirgendelcarmen.periodicowordpress.view.PostsListFragment
import com.iesvirgendelcarmen.periodicowordpress.viewmodel.wordpress.CategoryViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, PostListListener, SharePostListener {

    private val categoryViewModel by lazy {
        ViewModelProvider(this).get(CategoryViewModel::class.java)
    }

    lateinit var postsListFragment: PostsListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)
        addCategoriesToNavigationDrawer(navigationView)


        postsListFragment = PostsListFragment()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(
                R.id.container,
                postsListFragment
            ).commit()
        }
    }

    private fun addCategoriesToNavigationDrawer(navigationView: NavigationView) {
        categoryViewModel.categoryListLiveData.observe(this, Observer { resource ->

            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    val menu = navigationView.menu
                    val categoryMenu = menu.addSubMenu("Categorías")

                    for (category in resource.data) {
                        categoryMenu.add(Menu.NONE, category.id, Menu.NONE, category.name)
                    }

                    navigationView.invalidate()
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        postsListFragment.onSetCategory(item.itemId)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onClickPost(post: PostBO) {
        val bundle = Bundle()
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
}

interface SharePostListener {
    fun onClickSharePost(post: PostBO)
}