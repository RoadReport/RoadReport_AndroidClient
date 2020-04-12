package com.txwstudio.app.roadreport

import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreManager {

    /**
     * Add accident into firestore.
     *
     * @param currRoad: Current road code from accidentEventActivity.
     * @param data: The accident detail entered by user.
     *
     * @return If successful add data into firestore, return true.
     * TODO: Because of firebase's async, unable to return true.
     * */
    fun addAccident(currRoad: Int, data: AccidentData): Boolean {
        var successOrNot = false
        val db = FirebaseFirestore.getInstance()

        db.collection("ReportAccident").document(currRoad.toString())
            .collection("accidents").add(data)
            .addOnSuccessListener { documentReference ->
                Log.i("TESTTT", "DocumentSnapshot written with ID: ${documentReference.id}")
                successOrNot = true
            }
            .addOnFailureListener { e ->
                Log.i("TESTTT", "Error adding document", e)
                successOrNot = false
            }
        return successOrNot
    }

    /**
     * Get accident from firestore.
     *
     * @param currRoad: Current road code from accidentEventActivity.
     *
     * TODO: Implement
     * */
    fun getAccident(currRoad: Int) {
        val mutableList = mutableListOf<AccidentData>()
        val db = FirebaseFirestore.getInstance()

        db.collection("ReportAccident").document(currRoad.toString())
            .collection("accidents")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.i("TESTTT", "${document.id} => ${document.data}")
                    Log.i("TESTTT", document.data["location"].toString())
                    val dataToList = AccidentData(
                        document.data["userName"].toString(),
                        document.data["userUid"].toString(),
                        document.data["time"] as Timestamp,
                        document.data["situationType"] as Long,
                        document.data["location"].toString(),
                        document.data["situation"].toString()
                    )
                    mutableList.add(dataToList)
                }
            }
            .addOnFailureListener { exception ->
                Log.i("TESTTT", "Error getting documents: ", exception)
            }
    }
}