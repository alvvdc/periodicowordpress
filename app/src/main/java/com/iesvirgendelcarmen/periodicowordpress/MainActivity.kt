package com.iesvirgendelcarmen.periodicowordpress

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostBO
import com.iesvirgendelcarmen.periodicowordpress.view.PostDetailFragment
import com.iesvirgendelcarmen.periodicowordpress.view.PostListListener
import com.iesvirgendelcarmen.periodicowordpress.view.PostsListFragment

class MainActivity : AppCompatActivity(), PostListListener, SharePostListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(
                R.id.container,
                PostsListFragment()
            ).commit()
        }
    }

    override fun onClickPost(post: PostBO) {
        val bundle = Bundle()
        bundle.putParcelable("POST", post)

        val postDetailFragment = PostDetailFragment()
        postDetailFragment.arguments = bundle

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