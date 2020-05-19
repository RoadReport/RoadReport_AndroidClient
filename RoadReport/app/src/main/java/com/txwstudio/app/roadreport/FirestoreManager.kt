package com.txwstudio.app.roadreport

import android.content.Context
import android.util.Log
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
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

    /**
     * Delete accident from firestore using by document id.
     *
     * @param road Current road code
     * @param documentID The document to delete
     * @return isComplete If successful delete the doc, return true
     * */
    fun deleteAccident(roadCode: Int, documentId: String, isComplete: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("ReportAccident").document(roadCode.toString())
            .collection("accidents").document(documentId)
            .delete()
            .addOnSuccessListener {
                Log.i("TESTTT", "Success delete document $documentId")
            }
            .addOnFailureListener {
                Log.i("TESTTT", "Success fail to delete document $documentId")
                isComplete(false)
            }
            .addOnCompleteListener {
                Log.i("TESTTT", "Document deletion completed.")
                isComplete(true)
            }
    }

    /**
     * Get database query for Firestore Recycler View UI.
     *
     * @param roadCode Current road code
     *
     * @return database query
     */
    fun getRealtimeAccidentQuery(roadCode: Int): FirestoreRecyclerOptions<Accident?> {
        val db = FirebaseFirestore.getInstance()
            .collection("ReportAccident").document(roadCode.toString())
            .collection("accidents").orderBy("time", Query.Direction.DESCENDING)
        return FirestoreRecyclerOptions.Builder<Accident>()
            .setQuery(db, Accident::class.java)
            .build()
    }
}