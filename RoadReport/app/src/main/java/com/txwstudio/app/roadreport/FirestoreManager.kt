package com.txwstudio.app.roadreport

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.txwstudio.app.roadreport.model.Accident

class FirestoreManager {

    /**
     * Add accident into firestore.
     *
     * @param currRoad: Current road code from accidentEventActivity.
     * @param data: The accident detail entered by user.
     * */
    fun addAccident(currRoad: Int, data: Accident, isComplete: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("ReportAccident").document(currRoad.toString())
            .collection("accidents").add(data)
            .addOnSuccessListener { documentReference ->
                Log.i("TESTTT", "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.i("TESTTT", "Error adding document", e)
            }
            .addOnCompleteListener {
                isComplete(true)
            }
    }

    /**
     * Get accident from firestore.
     *
     * @param currRoad: Current road code from accidentEventActivity.
     *
     * TODO: Implement
     * */
    fun getAccident(currRoad: Int) {
        val mutableList = mutableListOf<Accident>()
        val db = FirebaseFirestore.getInstance()

        db.collection("ReportAccident").document(currRoad.toString())
            .collection("accidents").orderBy("time", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.i("TESTTT", "${document.id} => ${document.data}")
                    Log.i("TESTTT", document.data["location"].toString())
                    val dataToList = Accident(
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