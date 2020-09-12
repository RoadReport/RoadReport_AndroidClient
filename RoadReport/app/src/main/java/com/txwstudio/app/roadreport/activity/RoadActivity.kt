package com.txwstudio.app.roadreport.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.Util
import com.txwstudio.app.roadreport.firebase.AuthManager
import com.txwstudio.app.roadreport.ui.road.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_road2.*

class RoadActivity : AppCompatActivity() {

    private lateinit var fabAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        Util().setupTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_road2)
        setupToolBar()

        fabAdd = findViewById(R.id.fab_roadActivity)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.viewPager_roadActivity)
        val tabs: TabLayout = findViewById(R.id.tabs)

        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = 2
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (position != 0) fabAdd.hide() else fabAdd.show()
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        tabs.setupWithViewPager(viewPager)
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))
        fabAdd.setOnClickListener {
            if (AuthManager().userIsSignedIn()) {
                startActivity(Intent(this, EventEditorActivity::class.java))
            } else {
                Util().toast(this, getString(R.string.roadFrag_SignInFirst))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar_roadAct2)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = RoadCode().getCurrRoadName(this)
    }
}