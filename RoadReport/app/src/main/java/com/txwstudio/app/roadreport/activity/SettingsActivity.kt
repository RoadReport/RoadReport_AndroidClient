package com.txwstudio.app.roadreport.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.squareup.picasso.Picasso
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.Util
import com.txwstudio.app.roadreport.firebase.AuthManager
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupToolBar()
    }

    override fun onResume() {
        super.onResume()
//        val authStatus = AuthManager().userIsSignedIn()

        initAccountBlock()
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar_settings)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    /**
     * Setting up account block base on authStatus.
     *
     * @param authStatus True if user signed in.
     * */
    fun initAccountBlock() {
        val authStatus = AuthManager().userIsSignedIn()

        val user = AuthManager().getCurrUserModel()
        if (user != null) {

            Picasso.get()
                .load(user.photoUrl)
                .placeholder(R.drawable.ic_square_face_smiling_106dp)
                .error(R.drawable.ic_square_face_error_106dp)
                .into(imageView_settings_accountPhoto)

            textView_settings_accountName.text = user.displayName

            textView_settings_loginStatus.text =
                getString(R.string.settingsActivity_accountStatusLogin)

            cardView_settings_loginStatus.setOnClickListener {
                AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener {
                        restartActivity()
                        Util().toast(this, getString(R.string.settingsActivity_signOutSuccess))
                    }
            }

        } else {

            textView_settings_loginStatus.text =
                getString(R.string.settingsActivity_accountStatusLogout)

            cardView_settings_loginStatus.setOnClickListener {
                AuthManager().signIn(this)
            }
        }


        // User photo
//        Picasso.get()
//            .load(AuthManager().getCurrUserPhoto())
//            .placeholder(R.drawable.ic_square_face_error_106dp)
//            .error(R.drawable.ic_square_face_error_106dp)
//            .into(imageView_settings_accountPhoto)

        // User Name
//        textView_settings_accountName.text = AuthManager().getCurrUserName()

        // Setting sign in text
//        textView_settings_loginStatus.text =
//            if (authStatus) getString(R.string.settingsActivity_accountStatusLogin)
//            else getString(R.string.settingsActivity_accountStatusLogout)

//        cardView_settings_loginStatus.setOnClickListener {
//            if (authStatus) {
//                // Signed in
//                AuthUI.getInstance().signOut(this)
//                    .addOnCompleteListener {
//                        restartActivity()
//                        Util().toast(this, getString(R.string.settingsActivity_signOutSuccess))
//                    }
//            } else {
//                // Signed out
//                AuthManager().signIn(this)
//            }
//        }
    }


    /** Catch sign in result from FirebaseUI. */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AuthManager.RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                Util().toast(this, getString(R.string.settingsActivity_signInSuccess))
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Util().toast(this, getString(R.string.settingsActivity_signInFailed))
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
//                NavUtils.navigateUpFromSameTask(this)
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /** Called when sign out success. */
    fun restartActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }
}
