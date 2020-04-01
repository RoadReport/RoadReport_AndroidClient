package com.txwstudio.app.roadreport.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolBar()
        setOnClickListener()
    }


    private fun setupToolBar() {
        setSupportActionBar(toolbar_main)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }


    //TODO: MaterialCard onClick can cause crash, replace with setOnClickListener for now.
    private fun setOnClickListener() {
        card_1.setOnClickListener {
            RoadCode()
                .startActivityWithCode(this, RoadActivity(), RoadCode.ROADCODE_24)
        }
        card_2.setOnClickListener {
            RoadCode()
                .startActivityWithCode(this, RoadActivity(), RoadCode.ROADCODE_182)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
