package com.iesvirgendelcarmen.periodicowordpress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostBO
import com.iesvirgendelcarmen.periodicowordpress.view.PostDetailFragment
import com.iesvirgendelcarmen.periodicowordpress.view.PostListListener
import com.iesvirgendelcarmen.periodicowordpress.view.PostsListFragment

class MainActivity : AppCompatActivity(), PostListListener {

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

        supportFragmentManager.beginTransaction().replace(
            R.id.container,
            postDetailFragment
        ).addToBackStack(null).commit()
    }
}
