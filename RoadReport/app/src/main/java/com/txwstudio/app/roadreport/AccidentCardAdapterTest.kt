package com.txwstudio.app.roadreport

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        //TODO: Situation Type class
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
//        holder.situationType.text = accidentMutableList[position].situationType.toString()
        holder.location.text = model.location
        holder.situation.text = model.situation
        holder.time.text = formattedTime
        holder.userName.text = model.userName
    }
}