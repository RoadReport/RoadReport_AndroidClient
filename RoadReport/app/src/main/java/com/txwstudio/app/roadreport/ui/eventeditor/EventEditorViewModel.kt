package com.txwstudio.app.roadreport.ui.eventeditor

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.txwstudio.app.roadreport.firebase.AuthManager
import com.txwstudio.app.roadreport.firebase.FirestoreManager
import com.txwstudio.app.roadreport.model.Accident
import com.txwstudio.app.roadreport.model.AccidentEventParcelize
import java.text.SimpleDateFormat
import java.util.*

class EventEditorViewModel internal constructor(
    var editMode: Boolean,
    val roadCode: Int?,
    val roadName: String?,
    val documentId: String?,
//    var accidentModel: Accident?
    var accidentModel: AccidentEventParcelize?
) : ViewModel() {

    var currentRoadName = MutableLiveData<String>()

    // Detect user operation.
    val isSituationTypeButtonClicked = MutableLiveData<Boolean>()
    val isMapButtonClicked = MutableLiveData<Boolean>(false)
    val isUploadImageClicked = MutableLiveData<Boolean>(false)
    var errorNotSignedIn = MutableLiveData<Boolean>(false)
    var errorRequiredEntriesEmpty = MutableLiveData<Boolean>(false)
    var isSendingData = MutableLiveData<Boolean>(false)
    var isComplete = MutableLiveData<Boolean>()

    // Order by Accident Data Class, skip for userName, userUid and time.
    var situationType = MutableLiveData<Long>(0L)
    val locationText = MutableLiveData<String>()
    val locationGeoPoint = MutableLiveData<LatLng>()
    val situation = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String?>("")

    // Use to detect is double clicked or not.
    private var mLastClickTime = 0L

    fun init() {
        currentRoadName.value = roadName
        if (editMode) {
            setValueToLiveData()
        }
    }

    /**
     * Setting values to live data , invoke when editMode == true
     * */
    private fun setValueToLiveData() {
        situationType.value = accidentModel?.situationType
        locationText.value = accidentModel?.locationText
        if (accidentModel?.locationGeoPointLatitude != 0.0 && accidentModel?.locationGeoPointLongitude != 0.0) {
            locationGeoPoint.value = LatLng(accidentModel!!.locationGeoPointLatitude, accidentModel!!.locationGeoPointLongitude)
        }
        situation.value = accidentModel?.situation
        imageUrl.value = accidentModel?.imageUrl
    }

    /**
     * Fire situation type dialog.
     * */
    fun situationTypeClicked() {
        if (isSituationTypeButtonClicked.value == null) {
            isSituationTypeButtonClicked.value = false
        } else {
            isSituationTypeButtonClicked.value = !isSituationTypeButtonClicked.value!!
        }
    }

    /**
     * Change isMapButtonClicked status.
     * */
    fun mapButtonClicked() {
        if (locationGeoPoint.value == null) {
            isMapButtonClicked.value = true
        } else if (locationGeoPoint.value != null) {
            isMapButtonClicked.value = false
            locationGeoPoint.value = null
        }
    }

    /**
     * Change isUploadImageClicked status, invoke by button_eventEditor_uploadImage.
     * */
    fun uploadImageClicked() {
        // Prevent double click.
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        if (imageUrl.value.isNullOrBlank()) {
            isUploadImageClicked.value = true
        } else if (!imageUrl.value.isNullOrBlank()) {
            isUploadImageClicked.value = false
            imageUrl.value = ""
        }
    }

    fun letPrintSomeThing() {
        val w = getUserEntry()
        Log.i("TESTTT", "使用者名稱：${w.userName}")
        Log.i("TESTTT", "使用者ID：${w.userUid}")
        val time = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
            .format(w.time.toDate())
        Log.i("TESTTT", "時間：${time}")
        Log.i("TESTTT", "狀況代碼：${w.situationType}")
        Log.i("TESTTT", "地點：${w.locationText}")
        Log.i("TESTTT", "狀況描述：${w.situation}")
        Log.i("TESTTT", "圖片網址：${imageUrl.value}")
        Log.i("TESTTT", "uploadImageClicked：${isUploadImageClicked.value}")
        Log.i("TESTTT", "isRequiredEntriesEmpty：${isRequiredEntriesEmpty()}")
    }

    /**
     * Check required entries are not empty.
     *
     * @return true, if EMPTY
     * @return false, if NOT EMPTY
     * */
    private fun isRequiredEntriesEmpty(): Boolean {
        return situationType.value == 0L ||
                locationText.value.isNullOrBlank() ||
                situation.value.isNullOrBlank()
    }

    /**
     * Get current values.
     *
     * @return Accident
     * */
    private fun getUserEntry(): Accident {
        val currUser = AuthManager().getCurrUserModel()
        val event = Accident()
        if (!editMode) {
            // New event
            event.userName = currUser?.displayName!!
            event.userUid = currUser?.uid
            event.time = Timestamp(Date())
        } else if (editMode) {
            // Edit event
            event.userName = accidentModel!!.userName
            event.userUid = accidentModel!!.userUid
            event.time = accidentModel!!.time
        }

        event.situationType = situationType.value!!
        event.locationText = locationText.value.toString()
        locationGeoPoint.value?.let {
            event.locationGeoPoint = GeoPoint(it.latitude, it.longitude)
        }
        event.situation = situation.value.toString()
        event.imageUrl = imageUrl.value!!

        return event
    }

    /**
     * Start sending data to firebase firestore.
     * */
    fun sendClicked() {
        // Debug
        // letPrintSomeThing()

        // User not signed in yet, break.
        if (!AuthManager().isUserSignedIn()) {
            errorNotSignedIn.value = !errorNotSignedIn.value!!
            return
        }

        // Required entries are empty, break.
        if (isRequiredEntriesEmpty()) {
            errorRequiredEntriesEmpty.value = !errorRequiredEntriesEmpty.value!!
            return
        }

        // Everything looks great, start sending process.
        isSendingData.value = true
        if (!editMode) {
            // Perform add event.
            FirestoreManager().addAccident(roadCode!!, getUserEntry()) {
                isComplete.value = it
                if (!it) isSendingData.value = false
            }

        } else if (editMode) {
            // Perform update event.
            FirestoreManager().updateAccident(roadCode!!, documentId!!, getUserEntry()) {
                isComplete.value = it
                if (!it) isSendingData.value = false
            }
        }
    }
}