package com.txwstudio.app.roadreport.ui.eventeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.txwstudio.app.roadreport.model.AccidentEventParcelize

class EventEditorViewModelFactory(
    private val editMode: Boolean,
    private val roadCode: Int?,
    private val roadName: String?,
    private val documentId: String?,
//    private val accidentModel: Accident?
    private val accidentModel: AccidentEventParcelize?
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EventEditorViewModel(
            editMode,
            roadCode,
            roadName,
            documentId,
            accidentModel
        ) as T
    }
}