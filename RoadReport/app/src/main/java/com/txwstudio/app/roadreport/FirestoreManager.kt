package com.txwstudio.app.roadreport

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.txwstudio.app.roadreport.activity.AccidentEventActivity

class FirestoreManager {

    companion object {

    }


    fun addAccident(currRoad: Int, datas: AccidentData): Boolean {
        var successOrNot = false
        val db = FirebaseFirestore.getInstance()

        db.collection("ReportAccident").document(currRoad.toString())
            .collection("accidents").add(datas)
            .addOnCompleteListener { documentReference ->
                Log.i("TESTTT", "DocumentSnapshot written with ID: ${documentReference.result?.id}")
                successOrNot = true
//                Util().toast(AccidentEventActivity(), "Success")
            }
            .addOnFailureListener { e ->
                Log.i("TESTTT", "DocumentSnapshot written with ID: $e")
                successOrNot = false
            }

        return successOrNot
    }


}