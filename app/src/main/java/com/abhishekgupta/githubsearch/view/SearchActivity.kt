package com.abhishekgupta.githubsearch.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.abhishekgupta.githubsearch.R

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, SearchFragment.newInstance())
                    .commitNow()
        }
    }
}
