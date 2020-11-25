package com.txwstudio.app.roadreport.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.Util
import com.txwstudio.app.roadreport.adapter.LIVE_CAM_INDEX
import com.txwstudio.app.roadreport.adapter.ROAD_EVENT_INDEX
import com.txwstudio.app.roadreport.adapter.SectionsPagerAdapter
import com.txwstudio.app.roadreport.adapter.WEATHER_INDEX
import com.txwstudio.app.roadreport.firebase.AuthManager
import kotlinx.android.synthetic.main.activity_road.*

class RoadActivity : AppCompatActivity() {

    lateinit var fabAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        Util().setupTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_road)
        setupToolBar()

        val tabLayout: TabLayout = findViewById(R.id.tabLayout_roadActivity)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager_roadActivity)

        viewPager.adapter = SectionsPagerAdapter(this)
        viewPager.offscreenPageLimit = 2

        // Open wanted default fragment by user.
        val default =
            getSharedPreferences("main", MODE_PRIVATE).getString("defaultFragmentInRoadActivity", "0")
        if (default != null) {
            viewPager.currentItem = default.toInt()
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position != 0) fabAdd.hide() else fabAdd.show()
            }
        })

        // Set the text for each tab
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        subscribeUi()
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
        setSupportActionBar(toolbar_roadActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = RoadCode().getCurrRoadName(this)
    }

    private fun subscribeUi() {
        fabAdd = findViewById(R.id.fab_roadActivity)
        fabAdd.setOnClickListener {
            if (AuthManager().isUserSignedIn()) {
                startActivity(Intent(this, EventEditorActivity::class.java))
            } else {
                Util().toast(this, getString(R.string.roadFrag_SignInFirst))
            }
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            ROAD_EVENT_INDEX -> getString(R.string.roadActivity_tab_accidentEvent)
            WEATHER_INDEX -> getString(R.string.roadActivity_tab_weather)
            LIVE_CAM_INDEX -> getString(R.string.roadActivity_tab_liveCam)
            else -> "null"
        }
    }
}