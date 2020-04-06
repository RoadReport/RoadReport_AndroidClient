package com.txwstudio.app.roadreport

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.road_accident_row.view.*

class AccidentCardAdapter(private val accidentMutableList: MutableList<AccidentData>) :
    RecyclerView.Adapter<AccidentCardAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var situationType = itemView.textView_accidentCard_situationType as TextView
        var location = itemView.textView_accidentCard_location as TextView
        var situation = itemView.textView_accidentCard_situation as TextView
        var time = itemView.textView_accidentCard_time as TextView
        var userName = itemView.textView_accidentCard_userName as TextView
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // create a new view
        val roadAccidentRow1 = LayoutInflater.from(parent.context)
            .inflate(R.layout.road_accident_row, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return MyViewHolder(roadAccidentRow1)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.textView.text = accidentMutableList[position].situation
        holder.situationType.text = accidentMutableList[position].situationType.toString()
        holder.location.text = accidentMutableList[position].location
        holder.situation.text = accidentMutableList[position].situation
        holder.time.text = accidentMutableList[position].time.toDate().toString()
        holder.userName.text = accidentMutableList[position].userName
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = accidentMutableList.size

}