package com.txwstudio.app.roadreport.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.Util
import kotlinx.android.synthetic.main.activity_road2.*

class RoadActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Util().setupTheme(this)
        setupMaterialTransition()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_road2)
        setupToolBar()

        val bottomNavigationView: BottomNavigationView =
            findViewById(R.id.bottomNavView_roadActivity2)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer_roadActivity2) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
//            supportActionBar?.title = navController.currentDestination?.label
        }

        bottomNavigationView.setupWithNavController(navController)
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
        setSupportActionBar(toolbar_roadActivity2)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
//        supportActionBar?.title = RoadCode().getCurrRoadName(this)
    }
}