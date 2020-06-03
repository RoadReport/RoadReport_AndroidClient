package com.txwstudio.app.roadreport

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.txwstudio.app.roadreport.activity.AccidentEventActivity
import com.txwstudio.app.roadreport.model.Accident
import java.text.SimpleDateFormat
import java.util.*

class AccidentCardAdapterTest(val context: Context, val roadCode: Int) :
    FirestoreRecyclerAdapter<Accident?, AccidentCardHolder?>(
        FirestoreManager().getRealtimeAccidentQuery(roadCode)
    ) {

    private val uid = FirebaseAuthHelper().getCurrUserUid()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccidentCardHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.road_accident_row, parent, false)
        return AccidentCardHolder(view)
    }

    override fun onBindViewHolder(holder: AccidentCardHolder, position: Int, model: Accident) {
//        val formattedTime = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
//            .format(model.time.toDate())

        // Setting up each accident card's background and information.
        var backgroundType: Int = 0
        var situationType: Int = 0
        when (model.situationType.toInt()) {
            1 -> {
                backgroundType = R.drawable.bg_accident_type_1
                situationType = R.string.accidentEvent_situationType_1
            }
            2 -> {
                backgroundType = R.drawable.bg_accident_type_2
                situationType = R.string.accidentEvent_situationType_2
            }
            3 -> {
                backgroundType = R.drawable.bg_accident_type_3
                situationType = R.string.accidentEvent_situationType_3
            }
            4 -> {
                backgroundType = R.drawable.bg_accident_type_4
                situationType = R.string.accidentEvent_situationType_4
            }
            5 -> {
                backgroundType = R.drawable.bg_accident_type_4
                situationType = R.string.accidentEvent_situationType_5
            }
        }
        holder.layout.background = context.getDrawable(backgroundType)
        holder.situationType.text = context.getString(situationType)
        holder.location.text = model.location
        holder.situation.text = model.situation
        holder.time.text = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
            .format(model.time.toDate())
        holder.userName.text = model.userName


        // What user can to the card. Different onClick behavior for card.
        // Situation 1: NOT Signed in, no action at all.
        // Situation 2: Signed in && posted by user, Edit or Delete.
        // Situation 3: Signed in && NOT posted by user, Report.
        if (!FirebaseAuthHelper().userIsSignedIn()) {
            // Situation 1
            Log.i("TESTTT", "What user can to the card, Situation 1")
            Util().toast(context, "Situation 1 ，請先登入")

        } else if (FirebaseAuthHelper().userIsSignedIn() && model.userUid == uid) {
            // Situation 2, 0 for edit, 1 for delete.
            Log.i("TESTTT", "What user can to the card, Situation 2")
            holder.itemView.setOnLongClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setItems(R.array.roadActivity_cardLongClick) { _, which ->
                    when (which) {
                        0 -> {
                            val accidentModel = getItem(position)
                            val intent = Intent(context, AccidentEventActivity::class.java)
                            intent.putExtra("editMode", true)
                            intent.putExtra("documentId", snapshots.getSnapshot(position).id)
                            intent.putExtra("accidentModel", accidentModel)
                            context.startActivity(intent)
                        }
                        1 -> {
                            if (FirebaseAuthHelper().userIsSignedIn()
                                && FirebaseAuthHelper().getCurrUserUid() == getItem(position).userUid
                            ) {
                                FirestoreManager()
                                    .deleteAccident(roadCode, snapshots.getSnapshot(position).id) {
                                        Util().toast(context, if (it) "刪除成功" else "刪除失敗")

                                    }
                            } else {
                                Util().toast(context, "未登入或非你發布")
                            }
                        }
                    }
                }.show()

                Log.i("TESTTT", "onClick Doc ID: " + snapshots.getSnapshot(position).id)

                true
            }

        } else if (FirebaseAuthHelper().userIsSignedIn() && model.userUid != uid) {
            // Situation 3
            Log.i("TESTTT", "What user can to the card, Situation 3")
            Util().toast(context, "非你發布")
        }


    }
}