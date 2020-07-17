package com.txwstudio.app.roadreport.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.Util
import com.txwstudio.app.roadreport.model.Accident
import com.txwstudio.app.roadreport.ui.eventeditor.EventEditorFragment
import kotlinx.android.synthetic.main.activity_event_editor.*

class EventEditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_editor)
        setupToolBar()

        val editMode = intent.getBooleanExtra("editMode", false)
        val documentId = intent.getStringExtra("documentId")
        val accidentModel = intent.getParcelableExtra<Accident>("accidentModel")

        val bundle = Bundle()
        bundle.putBoolean("editMode", editMode)
        bundle.putInt("roadCode", RoadCode().getCurrentRoadCode(this))
        bundle.putString("documentId", documentId)
        bundle.putParcelable("accidentModel", accidentModel)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val eventEditorFragment = EventEditorFragment()
        eventEditorFragment.arguments = bundle

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
                finish()
                true
            }
            R.id.action_accidentEventDone -> {
                Util().toast(this, "TODO(完成)")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar_eventEditor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_activity_eventEditor)
    }
}
