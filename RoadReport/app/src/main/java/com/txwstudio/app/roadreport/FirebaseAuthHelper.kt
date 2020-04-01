package com.txwstudio.app.roadreport

import android.app.Activity
import androidx.core.app.ActivityCompat.startActivityForResult
import com.firebase.ui.auth.AuthUI

class FirebaseAuthHelper {

    companion object {
        const val RC_SIGN_IN = 0
    }

    fun checkLoginStatus() {

    }


    fun login(activity: Activity) {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.FacebookBuilder().build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
            activity, AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers).build(), RC_SIGN_IN, null
        )
    }

}