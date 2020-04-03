package com.txwstudio.app.roadreport.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.Timestamp
import com.txwstudio.app.roadreport.*
import kotlinx.android.synthetic.main.activity_accident_event.*
import java.util.*

class AccidentEventActivity : AppCompatActivity() {

    var ROADCODE = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accident_event)
        ROADCODE = RoadCode().getCurrentRoadCode(this)
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
            RoadCode.ROADCODE_24 -> text = getString(R.string.roadName_24)
            RoadCode.ROADCODE_182 -> text = getString(R.string.roadName_182)
            else -> text = getString(R.string.all_unknownError)
        }

        textView_accidentEvent_currentRoadContent.text = text
    }


    private fun getUserEntry(): AccidentData {
        return AccidentData(
            0,
            editText_accidentEvent_locationContent.text.toString(),
            editText_accidentEvent_situationContent.text.toString(),
            FirebaseAuthHelper().getCurrUserUid(),
            Timestamp(Date())
        )
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_accident_event, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // NavUtils.navigateUpFromSameTask(this)
                finish()
                true
            }
            R.id.action_accidentEventDone -> {
                if (FirebaseAuthHelper().checkSignInStatus()) {
                    if (FirestoreManager().addAccident(ROADCODE, getUserEntry())) {
                        Util().toast(this, getString(R.string.accidentEvent_addSuccess))
                        finish()
                    } else {
                        Util().toast(this, getString(R.string.accidentEvent_addFailed))
                    }
                } else {
                    // TODO: Redirect to login.
                    Util().toast(this, getString(R.string.all_unknownError))
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}