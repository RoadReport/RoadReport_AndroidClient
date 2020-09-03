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
import com.txwstudio.app.roadreport.StringCode
import com.txwstudio.app.roadreport.Util
import com.txwstudio.app.roadreport.databinding.FragmentEventEditorBinding
import com.txwstudio.app.roadreport.json.imgurupload.ImgurUploadJson
import com.txwstudio.app.roadreport.model.Accident
import com.txwstudio.app.roadreport.service.ImgurApi
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
    }

    private lateinit var eventEditorViewModel: EventEditorViewModel
    private lateinit var binding: FragmentEventEditorBinding
    private val UPLOAD_IMAGE_REQUEST_CODE = 1

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val bundle = this.arguments
        val editMode = bundle?.getBoolean(StringCode.EXTRA_NAME_EDIT_MODE, false)!!
        val roadCode = bundle.getInt(StringCode.EXTRA_NAME_ROAD_CODE, -1)
        val roadName = bundle.getString(StringCode.EXTRA_NAME_ROAD_NAME, "")
        val documentId = bundle.getString(StringCode.EXTRA_NAME_DOCUMENT_ID, "")
        val accidentModel = bundle.getParcelable<Accident>(StringCode.EXTRA_NAME_ACCIDENT_MODEL)
        Log.i("TESTTT", "${accidentModel?.location}")

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
     * Invoke by this:171
     *
     * Handle image callback and upload to imgur inside Fragment for now,
     * I'am limited by the technology of my time.
     * TODO(Fix pattern)
     * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
                }
                builder.create().show()
            }
        }

        eventEditorViewModel.situationType.observe(viewLifecycleOwner) {
            binding.editTextEventEditorSituationTypeContent.text =
                    Util().getSituationTypeName(requireContext(), it.toInt())
        }

        // When sending data to firestore, show progressBar.
        eventEditorViewModel.sendingData.observe(viewLifecycleOwner) {
            val builder: AlertDialog = AlertDialog.Builder(requireActivity())
                    .setView(R.layout.dialog_sending_data)
                    .setCancelable(false)
                    .create()
            if (it) {
                builder.show()
//                binding.progressbarEventEditorSendProgress.visibility = View.VISIBLE
            } else {
                builder.dismiss()
//                binding.progressbarEventEditorSendProgress.visibility = View.GONE
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

        // If send was clicked, but user didn't sign in, show msg.
        eventEditorViewModel.errorNotSignedIn.observe(viewLifecycleOwner) {
            if (it) {
                Util().snackBarShort(
                        requireActivity().findViewById(R.id.coordinatorLayout_eventEditor),
                        R.string.all_notSignedIn
                )
                eventEditorViewModel.errorNotSignedIn.value = false
            }
        }

        // If send was clicked, but required fields are empty, show msg.
        eventEditorViewModel.errorRequiredEntriesEmpty.observe(viewLifecycleOwner) {
            if (it) {
                Util().snackBarShort(
                        requireActivity().findViewById(R.id.coordinatorLayout_eventEditor),
                        R.string.accidentEvent_NoEntry
                )
                eventEditorViewModel.errorRequiredEntriesEmpty.value = false
            }
        }

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
            override fun onResponse(call: Call<ImgurUploadJson>, response: Response<ImgurUploadJson>) {
                binding.progressbarEventEditorImageProgress.visibility = View.GONE

                if (!response.isSuccessful) {
                    Util().snackBarShort(
                            requireActivity().findViewById(R.id.coordinatorLayout_eventEditor),
                            getString(R.string.accidentEvent_imageUploadFail_errorCode)
                                    + " ${response.code()}"
                    )
                } else if (response.isSuccessful) {
                    Log.i("TESTTT", "圖片連結為 " + response.body()?.getImageLink())
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

//        TODO(Testing new method, comment these out.)
//        GlobalScope.launch(Dispatchers.IO) {
//            val wow =
//                    ImgurApi.retrofitService.postImage("788cbd7c7cba9c1", body).execute().body()
//            withContext(Dispatchers.Main) {
//                eventEditorViewModel.imageUrl.value = wow?.data?.link.toString()
//                binding.progressbarEventEditorImageProgress.visibility = View.GONE
//            }
//        }
    }

    fun actionSendClicked() = eventEditorViewModel.sendClicked()

}