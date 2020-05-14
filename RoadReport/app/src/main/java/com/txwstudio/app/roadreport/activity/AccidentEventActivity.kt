package com.txwstudio.app.roadreport.activity

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.txwstudio.app.roadreport.*
import com.txwstudio.app.roadreport.model.Accident
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_accident_event, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // NavUtils.navigateUpFromSameTask(this)
                onBackPressed()
                true
            }
            R.id.action_accidentEventDone -> {
                sendEntryToFirestore()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setMessage(getString(R.string.accidentEvent_exitConfirm))
        builder.setPositiveButton(R.string.all_confirm) { _, _ ->
            finish()
        }
        builder.setNegativeButton(R.string.all_cancel) { _, _ -> }
        builder.show()
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar_accidentEvent)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupCurrentRoadContent() {
        textView_accidentEvent_currentRoadContent.text = RoadCode().getCurrRoadName(this)
    }


    /**
     * Choice what situation is.
     * */
    fun selectSituationType(v: View) {
        val builder = AlertDialog.Builder(this)
        builder.setItems(R.array.accidentEvent_situationTypeArray) { _, which ->
            situationType = which
            lateinit var text: String
            when (which) {
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

    private fun getUserEntry(): Accident {
        return Accident(
            FirebaseAuthHelper().getCurrUserName(),
            FirebaseAuthHelper().getCurrUserUid(),
            Timestamp(Date()),
            situationType.toLong(),
            editText_accidentEvent_locationContent.text.toString(),
            editText_accidentEvent_situationContent.text.toString()
        )
    }

    /** Validation then send data to firestore */
    private fun sendEntryToFirestore() {
        // User not sign in
        if (!FirebaseAuthHelper().userIsSignedIn()) {
            // TODO: Redirect to login.
            Util().toast(this, getString(R.string.all_notSignedIn))
//            startActivity(Intent(this, SettingsActivity::class.java))
            return
        }

        // Check entry
        if (userEntryIsEmpty()) {
            Util().toast(this, getString(R.string.accidentEvent_NoEntry))
            return
        }

        // TODO: Handle no connection situation (check firebase connection or something like that)

        // Send to firestore, addOnCompleteListener callback implemented.
        FirestoreManager().addAccident(ROADCODE, getUserEntry()) {
            if (it) {
                Util().toast(this, getString(R.string.accidentEvent_addSuccess))
                finish()
            } else {
                Util().toast(this, getString(R.string.accidentEvent_addFailed))
            }
        }
    }
}