package com.txwstudio.app.roadreport.ui.eventeditor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
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
        val roadCode = bundle.getInt("editMode", -1)
        val documentId = bundle.getString("documentId", "")
        val accidentModel = bundle.getParcelable<Accident>("accidentModel")
        Log.i("TESTTT", "${accidentModel?.location}")

        eventEditorViewModel = ViewModelProvider(
            this, EventEditorViewModelFactory(
                editMode,
                roadCode,
                RoadCode().getCurrRoadName(requireContext()),
                documentId,
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

        /**
         * Does this match MVVM Pattern? Not sure about that.
         * Anyway, it works, don't touch it for now.
         * TODO(I'am limited by the technology of my time.)
         * @link https://stackoverflow.com/questions/46727276/mvvm-pattern-and-startactivity
         * */
        binding.setClickListener {
            binding.editTextEventEditorSituationTypeContent.let {
                val builder = AlertDialog.Builder(requireContext())
                builder.setItems(R.array.accidentEvent_situationTypeArray) { _, which ->
                    eventEditorViewModel.situationType.value = which.toLong()
                    it.text = Util().getSituationTypeName(requireContext(), which)
                }
                builder.create().show()
            }

            binding.buttonEventEditorUploadImage.let {
                //TODO(Implement)
            }

        }
    }
}