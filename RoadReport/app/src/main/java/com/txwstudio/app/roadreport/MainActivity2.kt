package com.txwstudio.app.roadreport

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setupToolBar()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navHostFrag = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFrag.navController
//        val navController = findNavController(R.id.nav_host_fragment)

        navController.addOnDestinationChangedListener{ controller, destination, arguments ->
            textView_main2_toolbarTitle.text = when (destination.id) {
                R.id.navigation_home -> getString(R.string.mainFragment_title)
                R.id.navigation_account -> getString(R.string.accountFragment_title)
                else -> getString(R.string.mainFragment_title)
            }

        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_account)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar_main2)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}