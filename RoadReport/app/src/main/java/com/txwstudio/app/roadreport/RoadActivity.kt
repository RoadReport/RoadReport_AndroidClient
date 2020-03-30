package com.txwstudio.app.roadreport

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_road.*

class RoadActivity : AppCompatActivity() {

    var ROADCODE = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_road)
        ROADCODE = intent.extras!!.getInt("ROADCODE")
        setupToolBar()
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar_road)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var toolbarTitle = ""
        when(ROADCODE) {
            ROADCODE_24 -> toolbarTitle = getString(R.string.roadName_24)
            ROADCODE_182 -> toolbarTitle = getString(R.string.roadName_182)
            else -> toolbarTitle = getString(R.string.unknownError)
        }

        textView_road_toolbarTitle.text = toolbarTitle
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_road, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_addAccident -> {
                RoadCode().startActivityByCode(this, AccidentEventActivity(), ROADCODE)
//                startActivity(Intent(this, AccidentEventActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
