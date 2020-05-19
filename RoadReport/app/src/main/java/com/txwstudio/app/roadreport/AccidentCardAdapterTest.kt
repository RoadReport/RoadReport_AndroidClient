package com.txwstudio.app.roadreport

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.txwstudio.app.roadreport.model.Accident
import java.text.SimpleDateFormat
import java.util.*

class AccidentCardAdapterTest(val context: Context, val roadCode: Int) :
    FirestoreRecyclerAdapter<Accident?, AccidentCardHolder?>(
        FirestoreManager().getRealtimeAccidentQuery(roadCode)
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccidentCardHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.road_accident_row, parent, false)
        return AccidentCardHolder(view)
    }

    override fun onBindViewHolder(holder: AccidentCardHolder, position: Int, model: Accident) {
        val formattedTime = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
            .format(model.time.toDate())

        // Setting up each accident card information
        when (model.situationType.toInt()) {
            1 -> {
                holder.layout.background = context.getDrawable(R.drawable.bg_accident_type_1)
                holder.situationType.text =
                    context.getString(R.string.accidentEvent_situationType_1)
            }
            2 -> {
                holder.layout.background = context.getDrawable(R.drawable.bg_accident_type_2)
                holder.situationType.text =
                    context.getString(R.string.accidentEvent_situationType_2)
            }
            3 -> {
                holder.layout.background = context.getDrawable(R.drawable.bg_accident_type_3)
                holder.situationType.text =
                    context.getString(R.string.accidentEvent_situationType_3)
            }
            4 -> {
                holder.layout.background = context.getDrawable(R.drawable.bg_accident_type_4)
                holder.situationType.text =
                    context.getString(R.string.accidentEvent_situationType_4)
            }
        }
        holder.location.text = model.location
        holder.situation.text = model.situation
        holder.time.text = formattedTime
        holder.userName.text = model.userName

        model.userUid


        holder.itemView.setOnLongClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setItems(R.array.roadActivity_cardLongClick) { _, which ->
                when (which) {
                    0 -> Util().toast(context, "沒有功能")
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
            }
            builder.show()

            Log.i("TESTTT", "onClickEvent Doc ID: " + snapshots.getSnapshot(position).id)

            true
        }
    }
}