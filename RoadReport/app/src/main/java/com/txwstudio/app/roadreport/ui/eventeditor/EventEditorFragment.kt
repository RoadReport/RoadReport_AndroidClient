package com.txwstudio.app.roadreport.ui.eventeditor

import android.app.Activity
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
import androidx.lifecycle.observe
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.Util
import com.txwstudio.app.roadreport.databinding.FragmentEventEditorBinding
import com.txwstudio.app.roadreport.model.Accident
import com.txwstudio.app.roadreport.service.ImgurApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class EventEditorFragment : Fragment() {

    companion object {
        fun newInstance() = EventEditorFragment()
    }

    private lateinit var eventEditorViewModel: EventEditorViewModel
    private lateinit var binding: FragmentEventEditorBinding
    private val UPLOAD_IMAGE_REQUEST_CODE = 1

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                UPLOAD_IMAGE_REQUEST_CODE -> {
                    val fileUri = data.data
                    val takeFlags = (data.flags
                            and (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
                    requireContext().grantUriPermission(
                        requireContext().packageName,
                        fileUri,
                        takeFlags
                    )
                    fileUri?.let {
                        requireContext().contentResolver.takePersistableUriPermission(it, takeFlags)
                    }

                    requireContext().contentResolver.openInputStream(fileUri!!).use {
                        val file = File.createTempFile("prefix", ".er")
                        org.apache.commons.io.FileUtils.copyToFile(it, file)

                        val requestFile =
                            RequestBody.create(MediaType.parse("multipart/form-data"), file)
                        val body =
                            MultipartBody.Part.createFormData("image", file.name, requestFile)

                        sendImageAndGetLink(body)
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
        } else if (data == null) {
            Util().toast(requireContext(), getString(R.string.all_unknownError))
        }
    }

    fun subscribeUi() {
        // For Test, remove soon.
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
        }

        /**
         * When isUploadImageClicked is true start an intent to pick an image.
         * */
        eventEditorViewModel.isUploadImageClicked.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                startActivityForResult(intent, UPLOAD_IMAGE_REQUEST_CODE)
            }
        }

        /**
         * Observing imageUrl isNullorBlank in order to set background.
         * */
        eventEditorViewModel.imageUrl.observe(viewLifecycleOwner) {
            binding.buttonEventEditorUploadImage.let { its ->
                if (it.isNullOrBlank()) {
                    its.text =
                        requireContext().getString(R.string.accidentEvent_uploadImageTitle)
                    its.background =
                        requireContext().getDrawable(R.drawable.bg_upload_image_button)
                } else if (!it.isNullOrBlank()) {
                    its.text =
                        requireContext().getString(R.string.accidentEvent_removeUploadImageTitle)
                    its.background =
                        requireContext().getDrawable(R.drawable.bg_upload_image_remove_button)
                }
            }
        }
    }

    private fun sendImageAndGetLink(body: MultipartBody.Part) {
        GlobalScope.launch(Dispatchers.IO) {
            val wow =
                ImgurApi.retrofitService.postImage("788cbd7c7cba9c1", body).execute().body()
            withContext(Dispatchers.Main) {
                eventEditorViewModel.imageUrl.value = wow?.data?.link.toString()
            }
        }
    }
}