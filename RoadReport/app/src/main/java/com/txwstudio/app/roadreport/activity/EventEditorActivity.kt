package com.txwstudio.app.roadreport.activity

import android.os.Bundle
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ContentFrameLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.StringCode
import com.txwstudio.app.roadreport.Util
import com.txwstudio.app.roadreport.model.AccidentEventParcelize
import com.txwstudio.app.roadreport.ui.eventeditor.EventEditorFragment
import kotlinx.android.synthetic.main.activity_event_editor.*

class EventEditorActivity : AppCompatActivity() {

    private val eventEditorFragment = EventEditorFragment()
    private var mLastClickTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        Util().setupTheme(this)
        setupMaterialTransition()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_editor)

        // If start from RoadActivity(Add Event), passing == null.
        // If start from EventCardAdapter(Edit Current Event), passing == below.
        val editMode = intent.getBooleanExtra(StringCode.EXTRA_NAME_EDIT_MODE, false)
        val documentId = intent.getStringExtra(StringCode.EXTRA_NAME_DOCUMENT_ID)
        val accidentModel =
            intent.getParcelableExtra<AccidentEventParcelize>(StringCode.EXTRA_NAME_ACCIDENT_MODEL)
//        val accidentModel =
//            intent.getParcelableExtra<Accident>(StringCode.EXTRA_NAME_ACCIDENT_MODEL)

        setupToolBar(editMode)

        val bundle = Bundle()
        bundle.putBoolean(StringCode.EXTRA_NAME_EDIT_MODE, editMode)
        bundle.putInt(StringCode.EXTRA_NAME_ROAD_CODE, RoadCode().getCurrentRoadCodeFromSP(this))
        bundle.putString(StringCode.EXTRA_NAME_ROAD_NAME, RoadCode().getCurrRoadName(this))
        bundle.putString(StringCode.EXTRA_NAME_DOCUMENT_ID, documentId)
        bundle.putParcelable(StringCode.EXTRA_NAME_ACCIDENT_MODEL, accidentModel)

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

    private fun setupMaterialTransition() {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)

        // Set up shared element transition
        findViewById<ContentFrameLayout>(android.R.id.content).transitionName =
            "shared_element_end_root"
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = buildContainerTransform(true)
        window.sharedElementReturnTransition = buildContainerTransform(false)
    }

    private fun buildContainerTransform(entering: Boolean): MaterialContainerTransform? {
        val transform = MaterialContainerTransform()
        transform.setAllContainerColors(
            MaterialColors.getColor(findViewById(android.R.id.content), R.attr.colorSurface)
        )
        transform.addTarget(android.R.id.content)
        transform.duration = 300L
        transform.interpolator = FastOutSlowInInterpolator()
        transform.pathMotion = MaterialArcMotion()
        transform.fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
        transform.isDrawDebugEnabled = false
        return transform
    }

    private fun setupToolBar(isEditMode: Boolean) {
        setSupportActionBar(toolbar_eventEditor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (isEditMode) {
            getString(R.string.title_activity_eventEditor_editEvent)
        } else {
            getString(R.string.title_activity_eventEditor_addEvent)
        }
    }
}
