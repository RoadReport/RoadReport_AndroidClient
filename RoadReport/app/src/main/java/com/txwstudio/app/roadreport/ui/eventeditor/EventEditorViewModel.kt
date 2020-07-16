package com.txwstudio.app.roadreport.ui.eventeditor

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.txwstudio.app.roadreport.firebase.AuthManager
import com.txwstudio.app.roadreport.model.Accident
import java.text.SimpleDateFormat
import java.util.*

class EventEditorViewModel internal constructor(
    var editMode: Boolean,
    val roadCode: Int?,
    val roadName: String,
    val documentId: String?,
    var accidentModel: Accident?
) : ViewModel() {

    var currentRoadName = MutableLiveData<String>()

    // Order by Accident Data Class, skip for userName, userUid and time.
    var situationType = MutableLiveData<Long>(0L)
    val location = MutableLiveData<String>()
    val situation = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String?>()

    fun init() {
        currentRoadName.value = roadName
        imageUrl.value = "https://i.imgur.com/lbrHgFy.jpg"
        if (editMode) {
            Log.i("TESTTT", "編輯模式")
            setValueToLiveData()
        } else if (!editMode) {
            Log.i("TESTTT", "新增模式")

        }
    }

    private fun setValueToLiveData() {
        situationType.value = accidentModel?.situationType
        location.value = accidentModel?.location
        situation.value = accidentModel?.situation
        imageUrl.value = accidentModel?.imageUrl
    }

    fun letPrintSomeThing() {
        val w = getUserEntry()
        Log.i("TESTTT", "使用者名稱：${w.userName}")
        Log.i("TESTTT", "使用者ID：${w.userUid}")
        val time = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
            .format(w.time.toDate())
        Log.i("TESTTT", "時間：${time}")
        Log.i("TESTTT", "狀況代碼：${w.situationType}")
        Log.i("TESTTT", "地點：${w.location}")
        Log.i("TESTTT", "狀況描述：${w.situation}")
        Log.i("TESTTT", "圖片網址：${imageUrl.value}")
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
                location.value.isNullOrBlank() ||
                situation.value.isNullOrBlank()
    }


    fun getUserEntry(): Accident {
        val currUser = AuthManager().getCurrUserModel()
        return if (currUser != null) {
            Accident(
                currUser.displayName!!,
                currUser.uid,
                Timestamp(Date()),
                situationType.value!!,
                location.value.toString(),
                situation.value.toString(), ""
//                imageUrl.value!!
            )
        } else {
            Accident()
        }
    }

    private fun sendClicked() {
        if (isRequiredEntriesEmpty()) {
            return
        }

    }
}