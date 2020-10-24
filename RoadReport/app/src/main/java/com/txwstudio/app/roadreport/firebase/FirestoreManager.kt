package com.txwstudio.app.roadreport.firebase

import android.util.Log
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.txwstudio.app.roadreport.model.Accident

class FirestoreManager {

    // Parent Collection of accident event
    private val ReportAccident = "ReportAccident"

    // SubCollection of accident events
    private val accidents = "accidents"

    /**
     * Add accident into firestore.
     *
     * @param currRoad: Current road code from EventEditor.
     * @param data: The accident detail entered by user.
     * */
    fun addAccident(currRoad: Int, data: Accident, isComplete: (Boolean) -> Unit) {
        val db = Firebase.firestore

        db.collection(ReportAccident).document(currRoad.toString())
            .collection(accidents).add(data)
            .addOnSuccessListener { documentReference ->
                Log.i("TESTTT", "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.i("TESTTT", "Error adding document", e)
                isComplete(false)
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
//    fun getAccident(currRoad: Int) {
//        val mutableList = mutableListOf<Accident>()
//        val db = Firebase.firestore
//
//        db.collection("ReportAccident").document(currRoad.toString())
//            .collection("accidents").orderBy("time", Query.Direction.DESCENDING)
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.i("TESTTT", "${document.id} => ${document.data}")
//                    Log.i("TESTTT", document.data["location"].toString())
//                    val dataToList = Accident(
//                        document.data["userName"].toString(),
//                        document.data["userUid"].toString(),
//                        document.data["time"] as Timestamp,
//                        document.data["situationType"] as Long,
//                        document.data["location"].toString(),
//                        document.data["situation"].toString()
//                    )
//                    mutableList.add(dataToList)
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.i("TESTTT", "Error getting documents: ", exception)
//            }
//    }

    /**
     * Delete accident from firestore using by document id.
     *
     * @param roadCode Current road code
     * @param documentId The document need to delete
     * @return isComplete If successful delete the doc, return true
     * */
    fun deleteAccident(roadCode: Int, documentId: String, isComplete: (Boolean) -> Unit) {
        val db = Firebase.firestore

        db.collection(ReportAccident).document(roadCode.toString())
            .collection(accidents).document(documentId)
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
     * Delete accident from firestore using by document id.
     *
     * @param roadCode Current road code
     * @param documentId The document need to update
     * @param data New entry goes into firebase firestore
     * */
    fun updateAccident(
        roadCode: Int,
        documentId: String,
        data: Accident,
        isComplete: (Boolean) -> Unit
    ) {
        val db = Firebase.firestore
        val dataForUpdate = hashMapOf<String, Any?>(
            "situationType" to data.situationType,
            "locationText" to data.locationText,
            "locationGeoPoint" to data.locationGeoPoint,
            "situation" to data.situation,
            "imageUrl" to data.imageUrl
        )

        db.collection(ReportAccident).document(roadCode.toString())
            .collection(accidents).document(documentId)
            .update(dataForUpdate)
            .addOnSuccessListener {
                Log.i("TESTTT", "Success update document $documentId")
            }
            .addOnFailureListener {
                Log.i("TESTTT", "Success fail to update document $documentId")
                isComplete(false)
            }
            .addOnCompleteListener {
                Log.i("TESTTT", "Document update completed.")
                isComplete(true)
            }
    }

    /**
     * Get database query for Firestore Recycler View UI.
     *
     * @param roadCode Current road code
     * @return database query
     */
    fun getRealtimeAccidentQuery(roadCode: Int): FirestoreRecyclerOptions<Accident?> {
        val db = Firebase.firestore
            .collection(ReportAccident).document(roadCode.toString())
            .collection(accidents).orderBy("time", Query.Direction.DESCENDING)
        return FirestoreRecyclerOptions.Builder<Accident>()
            .setQuery(db, Accident::class.java)
            .build()
    }
}