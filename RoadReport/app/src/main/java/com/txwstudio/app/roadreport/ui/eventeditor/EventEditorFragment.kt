package com.txwstudio.app.roadreport.ui.eventeditor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.Util
import com.txwstudio.app.roadreport.databinding.FragmentEventEditorBinding
import com.txwstudio.app.roadreport.model.Accident

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

        val bundle = this.arguments
        val editMode = bundle?.getBoolean("editMode", false)!!
        val documentId = bundle.getString("documentId", "")
        val accidentModel = bundle.getParcelable<Accident>("accidentModel")
        Log.i("TESTTT", "${accidentModel?.location}")

        eventEditorViewModel = ViewModelProvider(
            this,
            EventEditorViewModelFactory(
                editMode,
                accidentModel
            )
        ).get(EventEditorViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_event_editor,
            container,
            false
        )
        binding.viewModel = eventEditorViewModel
        binding.lifecycleOwner = this

        eventEditorViewModel.init()

        subscribeUi()

        return binding.root
    }

    fun subscribeUi() {
        binding.clickMeToTest.setOnClickListener {
            eventEditorViewModel.letPrintSomeThing()
        }
    }
}