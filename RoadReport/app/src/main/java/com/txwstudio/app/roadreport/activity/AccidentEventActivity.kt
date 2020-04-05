package com.txwstudio.app.roadreport.activity

import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.component1
import com.google.firebase.Timestamp
import com.txwstudio.app.roadreport.*
import kotlinx.android.synthetic.main.activity_accident_event.*
import java.util.*


class AccidentEventActivity : AppCompatActivity() {

    private var ROADCODE = -1
    private var situationType = -1

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


    /**
     * Choice what situation is.
     * */
    fun selectSituationType(v: View) {
        val builder = AlertDialog.Builder(this)
        builder.setItems(R.array.accidentEvent_situationTypeArray) {_, which->
            situationType = which
            lateinit var text: String
            when(which) {
                0 -> text = getString(R.string.accidentEvent_situationType_0)
                1 -> text = getString(R.string.accidentEvent_situationType_1)
                2 -> text = getString(R.string.accidentEvent_situationType_2)
                3 -> text = getString(R.string.accidentEvent_situationType_3)
                4 -> text = getString(R.string.accidentEvent_situationType_4)
                5 -> text = getString(R.string.accidentEvent_situationType_5)
            }
            editText_accidentEvent_situationTypeContent.text = text
        }
        builder.create().show()
    }

    /** Are the entries empty?
     *
     * @return true: Yes, stop right there.
     * @return false: No, go to next step.
     * */
    private fun userEntryIsEmpty(): Boolean {
        return TextUtils.isEmpty(editText_accidentEvent_locationContent.text) ||
                TextUtils.isEmpty(editText_accidentEvent_situationContent.text) ||
                situationType == -1
    }

    private fun getUserEntry(): AccidentData {
        return AccidentData(
            FirebaseAuthHelper().getCurrUserName(),
            FirebaseAuthHelper().getCurrUserUid(),
            Timestamp(Date()),
            situationType,
            editText_accidentEvent_locationContent.text.toString(),
            editText_accidentEvent_situationContent.text.toString()
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
                    // Check user is signed in or not.
                    if (!userEntryIsEmpty()) {
                        // User did entered something, call FirestoreManager to store that data.
                        if (FirestoreManager().addAccident(ROADCODE, getUserEntry())) {
                            // TODO: Because of firebase's async, unable to return true here.
                            // Add data successfully
                            Util().toast(this, getString(R.string.accidentEvent_addSuccess))
                            finish()
                        } else {
                            // Add data failed
                            Util().toast(this, getString(R.string.accidentEvent_addFailed))
                        }
                    } else {
                        // User didn't entered anything.
                        Util().toast(this, getString(R.string.accidentEvent_NoEntry))
                    }

                } else {
                    // User didn't sign in yet
                    // TODO: Redirect to login.
                    Util().toast(this, getString(R.string.all_unknownError))
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}