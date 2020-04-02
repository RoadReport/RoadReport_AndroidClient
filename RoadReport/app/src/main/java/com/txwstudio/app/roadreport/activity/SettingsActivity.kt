package com.txwstudio.app.roadreport.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.txwstudio.app.roadreport.FirebaseAuthHelper
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.Util
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private var authStatus: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupToolBar()

        val array = arrayOf("服務條款", "隱私權政策")
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, array)
        listView_settings_general.adapter = arrayAdapter
    }

    override fun onResume() {
        super.onResume()
        authStatus = FirebaseAuthHelper().checkSignInStatus()
        setOnClickListener()
        updateSignInStatusUI(authStatus!!)
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar_settings)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    private fun setOnClickListener() {
        cardView_settings_loginStatus.setOnClickListener {
            if (authStatus!!) {
                // Signed in
                AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener {
                        restartActivity()
                        Util().toast(this, getString(R.string.settingsActivity_signOutSuccess))
                    }
            } else {
                // Signed out
                FirebaseAuthHelper().signIn(this)
            }
        }
    }


    /** Update sign in status text in card view. */
    private fun updateSignInStatusUI(result: Boolean) {
        if (result) {
            textView_settings_loginStatus.text =
                getString(R.string.settingsActivity_accountStatusLogin)
        } else {
            textView_settings_loginStatus.text =
                getString(R.string.settingsActivity_accountStatusLogout)
        }
    }


    /** Catch sign in result from FirebaseUI. */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FirebaseAuthHelper.RC_SIGN_IN) {
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
