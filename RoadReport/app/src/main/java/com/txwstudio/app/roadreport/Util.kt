package com.txwstudio.app.roadreport

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.txwstudio.app.roadreport.model.Accident

class Util {

    fun toast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    /**
     * Start accidentEventActivity by mode.
     * @param currActivity current activity
     * @param targetActivity target activity
     * @param editMode True if user wants to edit accident data
     * */
    fun startActivityByMode(
        currActivity: Context,
        targetActivity: Context,
        editMode: Boolean,
        model: Accident,
        documentId: String
    ) {
        val intent = Intent(currActivity, targetActivity::class.java)
        intent.putExtra("editMode", editMode)
        intent.putExtra("documentId", documentId)
        intent.putExtra("accidentModel", model)
        currActivity.startActivity(intent)
    }


    fun getSituationTypeName(context: Context, situationType: Int): String {
        return when (situationType) {
            0 -> context.getString(R.string.accidentEvent_situationType_0)
            1 -> context.getString(R.string.accidentEvent_situationType_1)
            2 -> context.getString(R.string.accidentEvent_situationType_2)
            3 -> context.getString(R.string.accidentEvent_situationType_3)
            4 -> context.getString(R.string.accidentEvent_situationType_4)
            5 -> context.getString(R.string.accidentEvent_situationType_5)
            else -> "errNameUnknown"
        }
    }

}