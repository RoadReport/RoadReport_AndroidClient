package com.txwstudio.app.roadreport.activity

import android.content.Intent
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.Util
import com.txwstudio.app.roadreport.firebase.AuthManager
import com.txwstudio.app.roadreport.ui.road.SectionsPagerAdapter

class RoadActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_road2)

        /** Original */
//        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
//
//        val viewPager: ViewPager = findViewById(R.id.viewPager_roadActivity)
//        viewPager.adapter = sectionsPagerAdapter
//
//        val tabs: TabLayout = findViewById(R.id.tabs)
//        tabs.setupWithViewPager(viewPager)

        /** Testing */
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)

        val viewPager: ViewPager = findViewById(R.id.viewPager_roadActivity)
        val tabs: TabLayout = findViewById(R.id.tabs)

        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = 2
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))

        tabs.setupWithViewPager(viewPager)
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))

        findViewById<FloatingActionButton>(R.id.fab_roadActivity).setOnClickListener {
                if (AuthManager().userIsSignedIn()) {
                    startActivity(Intent(this, AccidentEventActivity::class.java))
                } else {
                    Util().toast(this, getString(R.string.roadFrag_SignInFirst))
                }
            }
    }
}