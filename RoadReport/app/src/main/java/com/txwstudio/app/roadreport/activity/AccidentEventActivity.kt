package com.txwstudio.app.roadreport.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.FileUtils
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.Util
import com.txwstudio.app.roadreport.firebase.AuthManager
import com.txwstudio.app.roadreport.firebase.FirestoreManager
import com.txwstudio.app.roadreport.model.Accident
import com.txwstudio.app.roadreport.service.ImgurApi
import kotlinx.android.synthetic.main.activity_accident_event.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.InputStream
import java.net.URI
import java.util.*


class AccidentEventActivity : AppCompatActivity() {

    private var ROADCODE = -1
    private var situationType = -1
    private var mLastClickTime: Long = 0
    private var editMode = false
    private var imageUrl = ""
    private lateinit var accidentForEditing: Accident

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accident_event)
        ROADCODE = RoadCode().getCurrentRoadCode(this)
        setupToolBar()
        setupCurrentRoadText()

        uploadImage()
    }

    override fun onResume() {
        super.onResume()
        checkIsEditMode()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_accident_event, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // NavUtils.navigateUpFromSameTask(this)
                onBackPressed()
                true
            }
            R.id.action_accidentEventDone -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2500) {
                    Util().toast(this, getString(R.string.accidentEvent_dontDoubleClick))
                    return false
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                sendEntryToFirestore()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setMessage(getString(R.string.accidentEvent_exitConfirm))
        builder.setPositiveButton(R.string.all_confirm) { _, _ ->
            finish()
        }
        builder.setNegativeButton(R.string.all_cancel) { _, _ -> }
        builder.show()
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar_accidentEvent)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupCurrentRoadText() {
        textView_accidentEvent_currentRoadContent.text = RoadCode().getCurrRoadName(this)
    }


    /**
     * Check it's editing mode or not, if so setup content for later editing.*/
    private fun checkIsEditMode() {
        editMode = intent.getBooleanExtra("editMode", false)
        if (editMode) {
            accidentForEditing = intent.getParcelableExtra("accidentModel")
            setupCurrentRoadContent()
        }
    }

    private fun setupCurrentRoadContent() {
        Log.i("TESTTT", "setupCurrentRoadContent with $accidentForEditing")
        situationType = accidentForEditing.situationType.toInt()
        editText_accidentEvent_situationTypeContent.text =
            Util().getSituationTypeName(this, situationType)

        editText_accidentEvent_locationContent.setText(accidentForEditing.location)
        editText_accidentEvent_situationContent.setText(accidentForEditing.situation)
    }


    /**
     * Choice what situation is.
     * */
    fun selectSituationType(v: View) {
        val builder = AlertDialog.Builder(this)
        builder.setItems(R.array.accidentEvent_situationTypeArray) { _, which ->
            situationType = which
            editText_accidentEvent_situationTypeContent.text =
                Util().getSituationTypeName(this, which)
        }
        builder.create().show()
    }


    fun uploadImage() {
        textView_accidentEvent_uploadImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            // https://stackoverflow.com/questions/39953457/how-to-upload-image-file-in-retrofit-2

            val fileUri = data.data
            Log.i("TESTTT", "fileUri: $fileUri")

            val takeFlags = (data.flags
                    and (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
            this.grantUriPermission(this.packageName, fileUri, takeFlags)
            fileUri?.let {
                this.contentResolver.takePersistableUriPermission(it, takeFlags)
            }

            val fileUriPath = fileUri?.path
            Log.i("TESTTT", "fileUriPath：${fileUriPath}")


            contentResolver.openInputStream(fileUri!!).use {
                val itType = it

                val file = File.createTempFile("prefix", ".er")
                org.apache.commons.io.FileUtils.copyToFile(it, file)

                val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                dfgdfhjk(body)
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
        } else if (data == null) {
            Util().toast(this, getString(R.string.all_unknownError))
        }
    }


    fun dfgdfhjk(body: MultipartBody.Part) {
        GlobalScope.launch {
            val wow =
                ImgurApi.retrofitService.postImage("788cbd7c7cba9c1", body).execute().body()
            Log.i("TESTTT", "網址: ${wow?.data?.link}")

            withContext(Dispatchers.Main) {
                Glide.with(this@AccidentEventActivity)
                    .load(wow?.data?.link)
                    .into(imageView_accidentEvent_imageViewer)
            }
        }
    }

    /** Are the entries empty?
     *
     * @return true: Yes, stop right there.
     * @return false: No, go to next step.
     * */
    private fun userEntryIsEmpty(): Boolean {
        return TextUtils.isEmpty(editText_accidentEvent_locationContent.text) ||
                TextUtils.isEmpty(editText_accidentEvent_situationContent.text) ||
                situationType == -1
    }

    private fun getUserEntry(): Accident {
        val userInfo = AuthManager().getCurrUserModel()
        return Accident(
            userInfo?.displayName!!,
            userInfo.uid,
            Timestamp(Date()),
            situationType.toLong(),
            editText_accidentEvent_locationContent.text.toString(),
            editText_accidentEvent_situationContent.text.toString(),
            ""
        )
    }

    private fun getUserEntryAfterUpdate(): Accident {
        accidentForEditing.situationType = situationType.toLong()
        accidentForEditing.location = editText_accidentEvent_locationContent.text.toString()
        accidentForEditing.situation = editText_accidentEvent_situationContent.text.toString()
        return accidentForEditing
    }

    /** Validation then send data to firestore */
    private fun sendEntryToFirestore() {
        // User not sign in
        if (!AuthManager().userIsSignedIn()) {
            // TODO: Redirect to login.
            Util().toast(this, getString(R.string.all_notSignedIn))
//            startActivity(Intent(this, SettingsActivity::class.java))
            return
        }

        // Check entry
        if (userEntryIsEmpty()) {
            Util().toast(this, getString(R.string.accidentEvent_NoEntry))
            return
        }

        // TODO: Handle no connection situation (check firebase connection or something like that)


        // if editMode, update data on the firestore.
        // if !editMode, add new data to firestore.
        if (!editMode) {
            AlertDialog.Builder(this, R.style.AlertDialog)
                .setMessage(getString(R.string.accidentEvent_addConfirm))
                .setPositiveButton(R.string.all_confirm) { _, _ ->
                    FirestoreManager().addAccident(ROADCODE, getUserEntry()) {
                        if (it) {
                            Util().toast(this, getString(R.string.accidentEvent_addSuccess))
                            finish()
                        } else {
                            Util().toast(this, getString(R.string.accidentEvent_addFailed))
                        }
                    }
                }
                .setNegativeButton(R.string.all_cancel) { _, _ -> }
                .show()
        } else if (editMode) {
            AlertDialog.Builder(this, R.style.AlertDialog)
                .setMessage(getString(R.string.accidentEvent_editConfirm))
                .setPositiveButton(R.string.all_confirm) { _, _ ->
                    FirestoreManager().updateAccident(
                        ROADCODE,
                        intent.getStringExtra("documentId"),
                        getUserEntryAfterUpdate()
                    ) {
                        if (it) {
                            Util().toast(this, getString(R.string.accidentEvent_editSuccess))
                            finish()
                        } else {
                            Util().toast(this, getString(R.string.accidentEvent_editFailed))
                        }
                    }
                }
                .setNegativeButton(R.string.all_cancel) { _, _ -> }
                .show()
        }

    }


//        FirestoreManager().addAccident(ROADCODE, getUserEntry()) {
//            if (it) {
//                Util().toast(this, getString(R.string.accidentEvent_addSuccess))
//                finish()
//            } else {
//                Util().toast(this, getString(R.string.accidentEvent_addFailed))
//            }
//        }
}