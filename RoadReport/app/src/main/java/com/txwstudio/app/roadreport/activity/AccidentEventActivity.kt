package com.txwstudio.app.roadreport.activity

import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.txwstudio.app.roadreport.*
import com.txwstudio.app.roadreport.firebase.AuthManager
import com.txwstudio.app.roadreport.model.Accident
import kotlinx.android.synthetic.main.activity_accident_event.*
import java.util.*


class AccidentEventActivity : AppCompatActivity() {

    private var ROADCODE = -1
    private var situationType = -1
    private var mLastClickTime: Long = 0
    private var editMode = false
    private lateinit var accidentForEditing: Accident

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accident_event)
        ROADCODE = RoadCode().getCurrentRoadCode(this)
        setupToolBar()
        setupCurrentRoadText()

    }

    override fun onResume() {
        super.onResume()
        checkIsEditMode()
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
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2500) {
                    Util().toast(this, getString(R.string.accidentEvent_dontDoubleClick))
                    return false
                }
                mLastClickTime = SystemClock.elapsedRealtime()
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

    private fun setupCurrentRoadText() {
        textView_accidentEvent_currentRoadContent.text = RoadCode().getCurrRoadName(this)
    }


    /**
     * Check it's editing mode or not, if so setup content for later editing.*/
    private fun checkIsEditMode() {
        editMode = intent.getBooleanExtra("editMode", false)
        if (editMode) {
            accidentForEditing = intent.getParcelableExtra("accidentModel")
            setupCurrentRoadContent()
        }
    }

    private fun setupCurrentRoadContent() {
        Log.i("TESTTT", "setupCurrentRoadContent with $accidentForEditing")
        situationType = accidentForEditing.situationType.toInt()
        editText_accidentEvent_situationTypeContent.text =
            Util().getSituationTypeName(this, situationType)

        editText_accidentEvent_locationContent.setText(accidentForEditing.location)
        editText_accidentEvent_situationContent.setText(accidentForEditing.situation)
    }


    /**
     * Choice what situation is.
     * */
    fun selectSituationType(v: View) {
        val builder = AlertDialog.Builder(this)
        builder.setItems(R.array.accidentEvent_situationTypeArray) { _, which ->
            situationType = which
            editText_accidentEvent_situationTypeContent.text =
                Util().getSituationTypeName(this, which)
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
        val userInfo = AuthManager().getCurrUserModel()
        return Accident(
            userInfo?.displayName!!,
            userInfo.uid,
            Timestamp(Date()),
            situationType.toLong(),
            editText_accidentEvent_locationContent.text.toString(),
            editText_accidentEvent_situationContent.text.toString()
        )
    }

    private fun getUserEntryAfterUpdate(): Accident {
        accidentForEditing.situationType = situationType.toLong()
        accidentForEditing.location = editText_accidentEvent_locationContent.text.toString()
        accidentForEditing.situation = editText_accidentEvent_situationContent.text.toString()
        return accidentForEditing
    }

    /** Validation then send data to firestore */
    private fun sendEntryToFirestore() {
        // User not sign in
        if (!AuthManager().userIsSignedIn()) {
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


        // if editMode, update data on the firestore.
        // if !editMode, add new data to firestore.
        if (!editMode) {
            AlertDialog.Builder(this, R.style.AlertDialog)
                .setMessage(getString(R.string.accidentEvent_addConfirm))
                .setPositiveButton(R.string.all_confirm) { _, _ ->
                    FirestoreManager().addAccident(ROADCODE, getUserEntry()) {
                        if (it) {
                            Util().toast(this, getString(R.string.accidentEvent_addSuccess))
                            finish()
                        } else {
                            Util().toast(this, getString(R.string.accidentEvent_addFailed))
                        }
                    }
                }
                .setNegativeButton(R.string.all_cancel) { _, _ -> }
                .show()
        } else if (editMode) {
            AlertDialog.Builder(this, R.style.AlertDialog)
                .setMessage(getString(R.string.accidentEvent_editConfirm))
                .setPositiveButton(R.string.all_confirm) { _, _ ->
                    FirestoreManager().updateAccident(
                        ROADCODE,
                        intent.getStringExtra("documentId"),
                        getUserEntryAfterUpdate()) {
                        if (it) {
                            Util().toast(this, getString(R.string.accidentEvent_editSuccess))
                            finish()
                        } else {
                             Util().toast(this, getString(R.string.accidentEvent_editFailed))
                        }
                    }
                }
                .setNegativeButton(R.string.all_cancel) { _, _ -> }
                .show()
        }

    }


//        FirestoreManager().addAccident(ROADCODE, getUserEntry()) {
//            if (it) {
//                Util().toast(this, getString(R.string.accidentEvent_addSuccess))
//                finish()
//            } else {
//                Util().toast(this, getString(R.string.accidentEvent_addFailed))
//            }
//        }
}