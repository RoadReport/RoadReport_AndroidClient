package com.txwstudio.app.roadreport.ui.eventeditor

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.txwstudio.app.roadreport.model.Accident

class EventEditorViewModel internal constructor(
    editMode: Boolean,
    accidentModel: Accident?
) : ViewModel() {

    var editMode = editMode
    var accidentModel = accidentModel

    private val editModes = MutableLiveData<Boolean>(editMode)

    fun init() {
        if (editMode) {

        } else if (!editMode) {

        }
    }
}