package com.exa.android.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.exa.android.myapplication.Fragments.HistoryFragment
import com.exa.android.myapplication.Fragments.HomeFragment
import com.exa.android.myapplication.Fragments.OrderFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var btm_nav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btm_nav = findViewById(R.id.bottom_nav)

        if(savedInstanceState == null){
            loadFragment(HomeFragment())
            btm_nav.selectedItemId = R.id.home
        }

        btm_nav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home-> loadFragment(HomeFragment())
                R.id.order -> loadFragment(OrderFragment())
                R.id.transaction -> loadFragment(HistoryFragment())
            }
            true
        }

    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}