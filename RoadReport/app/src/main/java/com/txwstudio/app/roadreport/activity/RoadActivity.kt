package com.txwstudio.app.roadreport.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.Util
import com.txwstudio.app.roadreport.adapter.*
import com.txwstudio.app.roadreport.firebase.AuthManager
import kotlinx.android.synthetic.main.activity_road.*

class RoadActivity : AppCompatActivity() {

    lateinit var fabAdd: ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        Util().setupTheme(this)
        setupMaterialTransition()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_road)
        setupToolBar()

        val tabLayout: TabLayout = findViewById(R.id.tabLayout_roadActivity)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager_roadActivity)

        viewPager.adapter = SectionsPagerAdapter(this)
        viewPager.offscreenPageLimit = 3


        // Open wanted default fragment by user.
        val default =
            getSharedPreferences("main", MODE_PRIVATE).getString(
                "defaultFragmentInRoadActivity",
                "0"
            )
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

    private fun setupMaterialTransition() {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)

        // Set up shared element transition and disable overlay so views don't show above system bars
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
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
                val intent = Intent(this, EventEditorActivity::class.java)
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    it,
                    "shared_element_end_root"
                )
                startActivity(intent, options.toBundle())
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
            LOCAL_STORE_INDEX -> getString(R.string.roadActivity_tab_localStore)
            else -> "null"
        }
    }
}