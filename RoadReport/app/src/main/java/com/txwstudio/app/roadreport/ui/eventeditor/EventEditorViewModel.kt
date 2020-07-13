package com.txwstudio.app.roadreport.ui.eventeditor

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.txwstudio.app.roadreport.firebase.AuthManager
import com.txwstudio.app.roadreport.model.Accident
import kotlinx.android.synthetic.main.activity_accident_event.*
import java.util.*

class EventEditorViewModel internal constructor(
    editMode: Boolean,
    accidentModel: Accident?
) : ViewModel() {

    var editMode = editMode
    var accidentModel = accidentModel

    private val editModes = MutableLiveData<Boolean>(editMode)

    var situationType = MutableLiveData<Int>()
    val location = MutableLiveData<String>()
    val situation = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()

    fun init() {
        if (editMode) {

        } else if (!editMode) {

        }
    }

    fun letPrintSomeThing() {
        Log.i("TESTTT", "${location.value}")
    }


    fun userEntryIsEmpty(): Boolean {
        return location.value?.isBlank()!! || situation.value?.isBlank()!!

    }

    fun getUserEntry(): Accident {
        val currUser = AuthManager().getCurrUserModel()
        return if (currUser != null) {
            Accident(
                currUser.displayName!!,
                currUser.uid,
                Timestamp(Date()),
                situationType.value?.toLong()!!,
                location.value.toString(),
                situation.value.toString(),
                imageUrl.value!!
            )
        } else {
            Accident()
        }
    }

    /**
     * Check it is not an empty Accident()
     *
     * @param model Accident model that contains user's entry.
     *
     * @return true Yes, it is empty, abort.
     * @return false No, continue.
     * */
    fun isReturnedAccidentModelEmpty(model: Accident): Boolean {
        return model.location.isBlank() || model.situation.isBlank() || model.imageUrl.isBlank()
    }

    /**
     * do final check then send user entry to firestore.
     *
     * @param model Accident model that contains user's entry.
     * */
    fun sendEntryToFirestore(model: Accident) {
        
    }
}