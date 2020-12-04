package com.txwstudio.app.roadreport.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.txwstudio.app.roadreport.BuildConfig
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.Util
import com.txwstudio.app.roadreport.firebase.AuthManager
import kotlinx.android.synthetic.main.activity_main.*

// 抱抱 (っ･ω･)っ

class MainActivity : AppCompatActivity() {

    private var currentAppVersion = BuildConfig.VERSION_CODE

    override fun onCreate(savedInstanceState: Bundle?) {
        Util().setupTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)

        isNeedUpdate {
            if (it) {
                AlertDialog.Builder(this)
                    .setMessage(getString(R.string.mainActivity_needUpdateMsg))
                    .setPositiveButton(R.string.all_confirm) { _, _ ->
                        val appPackageName = packageName
                        try {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=$appPackageName")
                                )
                            )
                        } catch (anfe: ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                                )
                            )
                        }
                    }
                    .setNegativeButton(R.string.all_nope) { _, _ ->
                        finish()
                    }
                    .setOnDismissListener {
                        finish()
                    }
                    .show()
            }
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.nav_view)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            supportActionBar?.title = navController.currentDestination?.label
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_account
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavigationView.setupWithNavController(navController)

        AuthManager().isUserSignedIn()
        MobileAds.initialize(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Compare app version with the ANDROID_MIN_VER on the Firebase.
     * */
    private fun isNeedUpdate(needUpdate: (Boolean) -> Unit) {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        })
        remoteConfig.fetchAndActivate().addOnSuccessListener {
            if (currentAppVersion.toLong() >= remoteConfig["ANDROID_MIN_VER"].asLong()) {
                needUpdate(false)
            } else {
                needUpdate(true)
            }
        }.addOnFailureListener {

        }
    }
}