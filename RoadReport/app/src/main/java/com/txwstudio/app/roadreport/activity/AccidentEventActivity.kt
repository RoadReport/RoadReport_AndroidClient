package com.txwstudio.app.roadreport.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.Util
import kotlinx.android.synthetic.main.activity_accident_event.*

class AccidentEventActivity : AppCompatActivity() {

    var ROADCODE = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accident_event)
        ROADCODE = intent.extras!!.getInt("ROADCODE")
        setupToolBar()
        setupCurrentRoadContent()
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar_accidentEvent)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupCurrentRoadContent() {
        var text = ""
        when (ROADCODE) {
            RoadCode.ROADCODE_24 -> text = getString(
                R.string.roadName_24
            )
            RoadCode.ROADCODE_182 -> text = getString(
                R.string.roadName_182
            )
            else -> text = getString(R.string.unknownError)
        }

        textView_accidentEvent_currentRoadContent.text = text
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_accident_event, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
    //                NavUtils.navigateUpFromSameTask(this)
                finish()
                true
            }
            R.id.action_accidentEventDone -> {
                Util().toast(this, "Done")
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
