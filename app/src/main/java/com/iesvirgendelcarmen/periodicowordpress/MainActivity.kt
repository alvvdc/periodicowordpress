package com.iesvirgendelcarmen.periodicowordpress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.iesvirgendelcarmen.periodicowordpress.view.PostsListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().add(
            R.id.container,
            PostsListFragment()
        ).commit()
    }
}
