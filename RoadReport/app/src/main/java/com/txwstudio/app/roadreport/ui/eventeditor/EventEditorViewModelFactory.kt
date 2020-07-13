package com.txwstudio.app.roadreport.ui.eventeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.txwstudio.app.roadreport.model.Accident

class EventEditorViewModelFactory(
    private val editMode: Boolean,
    private val accidentModel: Accident?
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EventEditorViewModel(
            editMode,
            accidentModel
        ) as T
    }
}