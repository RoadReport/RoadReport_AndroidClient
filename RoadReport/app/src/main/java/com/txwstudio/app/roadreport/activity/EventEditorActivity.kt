package com.txwstudio.app.roadreport.activity

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.Util
import com.txwstudio.app.roadreport.model.Accident
import com.txwstudio.app.roadreport.ui.eventeditor.EventEditorFragment
import kotlinx.android.synthetic.main.activity_event_editor.*

class EventEditorActivity : AppCompatActivity() {

    private val eventEditorFragment = EventEditorFragment()
    private var mLastClickTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        Util().setupTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_editor)
        setupToolBar()

        // If start from RoadActivity(Add Event), passing == null.
        // If start from AccidentCardAdapter(Edit Current Event), passing == below.
        val editMode = intent.getBooleanExtra("editMode", false)
        val documentId = intent.getStringExtra("documentId")
        val accidentModel = intent.getParcelableExtra<Accident>("accidentModel")

        val bundle = Bundle()
        bundle.putBoolean("editMode", editMode)
        bundle.putInt("roadCode", RoadCode().getCurrentRoadCode(this))
        bundle.putString("roadName", RoadCode().getCurrRoadName(this))
        bundle.putString("documentId", documentId)
        bundle.putParcelable("accidentModel", accidentModel)

        eventEditorFragment.arguments = bundle

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragView_eventEditor, eventEditorFragment)
        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_accident_event, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_accidentEventDone -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                    Util().snackBarShort(
                        coordinatorLayout_eventEditor,
                        R.string.accidentEvent_dontDoubleClick
                    )
                    return false
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                eventEditorFragment.actionSendClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.accidentEvent_exitConfirm))
        builder.setPositiveButton(R.string.all_confirm) { _, _ ->
            super.onBackPressed()
        }
        builder.setNegativeButton(R.string.all_cancel) { _, _ -> }
        builder.show()
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar_eventEditor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_activity_eventEditor)
    }
}
