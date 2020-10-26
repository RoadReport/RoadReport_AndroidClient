package com.txwstudio.app.roadreport.ui.eventeditor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.StringCode
import com.txwstudio.app.roadreport.Util
import com.txwstudio.app.roadreport.databinding.FragmentEventEditorBinding
import com.txwstudio.app.roadreport.json.imgurupload.ImgurUploadJson
import com.txwstudio.app.roadreport.model.AccidentEventParcelize
import com.txwstudio.app.roadreport.service.ImgurApi
import com.txwstudio.app.roadreport.ui.maps.AddGeoPointViewModel
import com.txwstudio.app.roadreport.ui.maps.MapsFragment
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException

class EventEditorFragment : Fragment() {

    companion object {
        fun newInstance() = EventEditorFragment()
        private val UPLOAD_IMAGE_REQUEST_CODE = 1
    }

    // Base ViewModel and DataBinding
    private lateinit var eventEditorViewModel: EventEditorViewModel
    private lateinit var binding: FragmentEventEditorBinding

    // Shared view model between EventEditorFragment and MapsFragment.
    private val addGeoPointViewModel: AddGeoPointViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val bundle = this.arguments
        val editMode = bundle?.getBoolean(StringCode.EXTRA_NAME_EDIT_MODE, false)!!
        val roadCode = bundle.getInt(StringCode.EXTRA_NAME_ROAD_CODE, -1)
        val roadName = bundle.getString(StringCode.EXTRA_NAME_ROAD_NAME, "")
        val documentId = bundle.getString(StringCode.EXTRA_NAME_DOCUMENT_ID, "")
//        val accidentModel = bundle.getParcelable<Accident>(StringCode.EXTRA_NAME_ACCIDENT_MODEL)
        val accidentModel =
            bundle.getParcelable<AccidentEventParcelize>(StringCode.EXTRA_NAME_ACCIDENT_MODEL)

        eventEditorViewModel = ViewModelProvider(
            this, EventEditorViewModelFactory(
                editMode,
                roadCode,
                roadName,
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

    /**
     * Handle image callback and upload to imgur inside Fragment for now,
     * I'am limited by the technology of my time.
     * TODO(Fix pattern)
     * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            UPLOAD_IMAGE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    when (requestCode) {
                        UPLOAD_IMAGE_REQUEST_CODE -> {
                            // Show infinite progress bar, wow much infinite!
                            binding.progressbarEventEditorImageProgress.visibility = View.VISIBLE

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
                                requireContext().contentResolver.takePersistableUriPermission(
                                    it,
                                    takeFlags
                                )
                            }

                            requireContext().contentResolver.openInputStream(fileUri!!).use {
                                val file = File.createTempFile("prefix", ".er")
                                org.apache.commons.io.FileUtils.copyToFile(it, file)

                                val requestFile =
                                    RequestBody.create(MediaType.parse("multipart/form-data"), file)
                                val body =
                                    MultipartBody.Part.createFormData(
                                        "image",
                                        file.name,
                                        requestFile
                                    )

                                sendImageAndGetLink(body)
                            }
                        }
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                } else if (data == null) {
                    Util().toast(requireContext(), getString(R.string.all_unknownError))
                }
            }
        }
    }

    /**
     * Does this match MVVM Pattern? Not sure about that.
     * Anyway, it works, don't touch it for now.
     * TODO(I'am limited by the technology of my time.)
     * @link https://stackoverflow.com/questions/46727276/mvvm-pattern-and-startactivity
     * */
    fun subscribeUi() {
        /**
         * Binding
         * */
        // Open situation type dialog
        binding.editTextEventEditorSituationTypeContent.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setItems(R.array.accidentEvent_situationTypeArray) { _, which ->
                eventEditorViewModel.situationType.value = which.toLong()
            }
            builder.create().show()
        }

        // Map button, picking geo point or clean it.
        binding.imageViewEventEditorLocationMapButton.setOnClickListener {
            if (eventEditorViewModel.locationGeoPoint.value == null) {
                MapsFragment().show(
                    requireActivity().supportFragmentManager,
                    MapsFragment::class.java.simpleName
                )
            } else if (eventEditorViewModel.locationGeoPoint.value != null) {
                eventEditorViewModel.locationGeoPoint.value = null
            }
        }

        /**
         * Observing LiveData inside view model.
         * */
        // Set text for situation type
        eventEditorViewModel.situationType.observe(viewLifecycleOwner) {
            binding.editTextEventEditorSituationTypeContent.text =
                Util().getSituationTypeName(requireContext(), it.toInt())
        }

        // Observe sharedViewModel for new LatLng
        addGeoPointViewModel.sharedLocationGeoPoint.observe(viewLifecycleOwner) {
            Util().snackBarLong(requireView(), "${it.latitude} ${it.longitude}")
            eventEditorViewModel.locationGeoPoint.value = it
        }

        // Set icon for map button
        eventEditorViewModel.locationGeoPoint.observe(viewLifecycleOwner) {
            binding.imageViewEventEditorLocationMapButton.let { its ->
                if (it == null) {
                    // Set icon to map
                    its.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_outline_map_24
                        )
                    )
                } else {
                    // Set icon to clear
                    its.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_clear_24
                        )
                    )
                }
            }
        }

        // A clickListener for picking image, if it value == true.
        // TODO(Set onClickListener direct, not from observe)
        eventEditorViewModel.isUploadImageClicked.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                startActivityForResult(intent, UPLOAD_IMAGE_REQUEST_CODE)
            }
        }

        // Observing imageUrl isNullOrBlank in order to set image to ImageView.
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

        // A snack bar for not signed in message.
        eventEditorViewModel.errorNotSignedIn.observe(viewLifecycleOwner) {
            if (it) {
                Util().snackBarShort(
                    requireActivity().findViewById(R.id.coordinatorLayout_eventEditor),
                    R.string.all_notSignedIn
                )
                eventEditorViewModel.errorNotSignedIn.value = false
            }
        }

        // A snack bar for empty entries message.
        eventEditorViewModel.errorRequiredEntriesEmpty.observe(viewLifecycleOwner) {
            if (it) {
                Util().snackBarShort(
                    requireActivity().findViewById(R.id.coordinatorLayout_eventEditor),
                    R.string.accidentEvent_NoEntry
                )
                eventEditorViewModel.errorRequiredEntriesEmpty.value = false
            }
        }

        // A dialog for sending operation.
        eventEditorViewModel.isSendingData.observe(viewLifecycleOwner) {
            val builder: AlertDialog = AlertDialog.Builder(requireActivity())
                .setView(R.layout.dialog_sending_data)
                .setCancelable(false)
                .create()
            if (it) {
                builder.show()
            } else {
                builder.dismiss()
            }
        }

        // Close EventEditor if the process is complete.
        eventEditorViewModel.isComplete.observe(viewLifecycleOwner) {
            if (it) {
                requireActivity().finish()
            } else {
                Util().snackBarShort(
                    requireActivity().findViewById(R.id.coordinatorLayout_eventEditor),
                    R.string.eventEditor_eventAddUpdateFailure
                )
            }
        }
    }

    private fun sendImageAndGetLink(body: MultipartBody.Part) {
        val imgurApi = ImgurApi.retrofitService.postImage("788cbd7c7cba9c1", body)
        imgurApi.enqueue(object : Callback<ImgurUploadJson> {
            override fun onResponse(
                call: Call<ImgurUploadJson>,
                response: Response<ImgurUploadJson>
            ) {
                binding.progressbarEventEditorImageProgress.visibility = View.GONE

                if (!response.isSuccessful) {
                    Util().snackBarShort(
                        requireActivity().findViewById(R.id.coordinatorLayout_eventEditor),
                        getString(R.string.accidentEvent_imageUploadFail_errorCode)
                                + " ${response.code()}"
                    )
                } else if (response.isSuccessful) {
                    eventEditorViewModel.imageUrl.value = response.body()?.getImageLink()
                }
            }

            override fun onFailure(call: Call<ImgurUploadJson>, t: Throwable) {
                binding.progressbarEventEditorImageProgress.visibility = View.GONE

                val errMsg: String = when (t) {
                    is SocketTimeoutException -> {
                        getString(R.string.accidentEvent_imageUploadFail_timeout)
                    }
                    is IOException -> {
                        getString(R.string.accidentEvent_imageUploadFail_IO)
                    }
                    else -> getString(R.string.accidentEvent_imageUploadFail_unknown)
                }
                Util().snackBarShort(
                    requireActivity().findViewById(R.id.coordinatorLayout_eventEditor),
                    errMsg
                )
            }
        })
    }

    fun actionSendClicked() = eventEditorViewModel.sendClicked()

}