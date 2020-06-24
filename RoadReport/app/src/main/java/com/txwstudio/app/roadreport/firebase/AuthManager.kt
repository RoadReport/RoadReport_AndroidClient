package com.txwstudio.app.roadreport.firebase

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.txwstudio.app.roadreport.R

class AuthManager {

    companion object {
        const val RC_SIGN_IN = 0
    }

    /** Check sign in status.
     * @return true: Signed in.
     * @return false: Not Signed in yet. */
    fun userIsSignedIn(): Boolean {
        val auth = Firebase.auth.currentUser
        return auth != null
    }

    fun getCurrUserModel(): FirebaseUser? {
        return Firebase.auth.currentUser
    }

    /**
     * Sign in using FirebaseUI
     * @link https://firebase.google.com/docs/auth/android/firebaseui
     * */
    fun signIn(activity: Activity) {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.FacebookBuilder().build()
        )

        // Create and launch sign-in intent
        ActivityCompat.startActivityForResult(
            activity, AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(), RC_SIGN_IN, null
        )
    }

    /** Sign out direct called in fragment. */
    fun signOut(context: Context) {
        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener {
                Toast.makeText(context, R.string.settingsActivity_signOutSuccess, Toast.LENGTH_SHORT).show()
            }
    }
}