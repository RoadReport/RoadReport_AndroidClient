package com.txwstudio.app.roadreport.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.txwstudio.app.roadreport.BuildConfig
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.firebase.AuthManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFrag.navController

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
        navView.setupWithNavController(navController)

        AuthManager().userIsSignedIn()

        checkAppVersion {
            if (it) {
                AlertDialog.Builder(this, R.style.AlertDialog)
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
    }

    /**
     * Compare app version with ANDROID_MIN_VER.
     * */
    var currentAppVersion = BuildConfig.VERSION_CODE
    private fun checkAppVersion(needUpdate: (Boolean) -> Unit) {
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