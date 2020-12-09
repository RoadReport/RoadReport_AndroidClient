package com.txwstudio.app.roadreport.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.Util
import kotlinx.android.synthetic.main.activity_road.*

class RoadActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Util().setupTheme(this)
        setupMaterialTransition()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_road2)
        setupToolBar()
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
}