package com.txwstudio.app.roadreport.ui.eventeditor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.databinding.FragmentEventEditorBinding

class EventEditorFragment : Fragment() {

    companion object {
        fun newInstance() = EventEditorFragment()
    }

    private lateinit var eventEditorViewModel: EventEditorViewModel
    private lateinit var binding: FragmentEventEditorBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        eventEditorViewModel = ViewModelProvider(this).get(EventEditorViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_event_editor,
            container,
            false
        )
        binding.viewModel = eventEditorViewModel
        binding.lifecycleOwner = this

        return binding.root
    }

}