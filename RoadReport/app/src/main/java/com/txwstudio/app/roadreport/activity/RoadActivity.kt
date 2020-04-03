package com.txwstudio.app.roadreport.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.txwstudio.app.roadreport.FirebaseAuthHelper
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.Util
import kotlinx.android.synthetic.main.activity_road.*

class RoadActivity : AppCompatActivity() {

    var ROADCODE = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_road)
        ROADCODE = RoadCode().getCurrentRoadCode(this)
        setupToolBar()
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar_road)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var toolbarTitle = ""
        when(ROADCODE) {
            RoadCode.ROADCODE_24 -> toolbarTitle = getString(
                R.string.roadName_24
            )
            RoadCode.ROADCODE_182 -> toolbarTitle = getString(
                R.string.roadName_182
            )
            else -> toolbarTitle = getString(R.string.all_unknownError)
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
                if (FirebaseAuthHelper().checkSignInStatus()) {
                    RoadCode().startActivityWithCode(this,
                        AccidentEventActivity(), ROADCODE)
//                startActivity(Intent(this, AccidentEventActivity::class.java))
                } else {
                    Util().toast(this, getString(R.string.roadActivity_SignInFirst))
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
