package com.txwstudio.app.roadreport.ui.roadevent

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.txwstudio.app.roadreport.adapter.AccidentCardAdapter
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode

class RoadFragment : Fragment() {

    companion object {
        fun newInstance() = RoadFragment()
    }

    private lateinit var viewModel: RoadViewModel
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var viewAdapterRealtimeClass: AccidentCardAdapter
    private var ROADCODE = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ROADCODE = RoadCode().getCurrentRoadCode(context)
        layoutManager = LinearLayoutManager(context)
        viewAdapterRealtimeClass = AccidentCardAdapter(context, ROADCODE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_road, container, false)

        val recyclerView = root.findViewById<RecyclerView>(R.id.recyclerView_roadFrag)
        recyclerView.apply {
            layoutManager = this@RoadFragment.layoutManager
            adapter = this@RoadFragment.viewAdapterRealtimeClass
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RoadViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onResume() {
        super.onResume()
        viewAdapterRealtimeClass.startListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        /**
         * In order to create the better UX,
         * the adapter will stop listening after user exit current activity.
         * */
        viewAdapterRealtimeClass.stopListening()
    }

}