package com.txwstudio.app.roadreport.ui.roadevent2

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.adapter.EventCardAdapter
import com.txwstudio.app.roadreport.databinding.FragmentRoadEventBinding

class RoadEventFragment : Fragment() {

    companion object {
        fun newInstance() = RoadEventFragment()
    }

    private lateinit var binding: FragmentRoadEventBinding

    private lateinit var adapter: EventCardAdapter

    private val roadEventViewModel: RoadEventViewModel by viewModels()

    private var ROADCODE = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ROADCODE = RoadCode().getCurrentRoadCodeFromSP(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoadEventBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter =
            EventCardAdapter(requireView(), requireActivity().supportFragmentManager, ROADCODE)
        binding.recyclerViewRoadEventFrag.adapter = adapter
        subscribeUi()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.stopListening()
    }

    private fun subscribeUi() {
        adapter.isEventEmpty.observe(viewLifecycleOwner) {
            if (it) {
                binding.linearLayoutRoadEventFrag.visibility = View.VISIBLE
            } else {
                binding.linearLayoutRoadEventFrag.visibility = View.GONE
            }
        }
    }

}