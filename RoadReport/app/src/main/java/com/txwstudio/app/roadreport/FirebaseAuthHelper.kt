package com.txwstudio.app.roadreport

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.txwstudio.app.roadreport.activity.SettingsActivity

class FirebaseAuthHelper {

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

    fun getCurrUserName(): String {
        val auth = Firebase.auth.currentUser
        return if (auth != null) {
            Log.i("TESTTT", "Current user name: " + auth.displayName)
            auth.displayName!!
        } else {
            ""
        }
    }

    fun getCurrUserUid(): String {
        val auth = Firebase.auth.currentUser
        return if (auth != null) {
            Log.i("TESTTT", "Current user UID: " + auth.uid)
            auth.uid
        } else {
            ""
        }
    }

    fun getCurrUserPhoto(): Uri? {
        val auth = Firebase.auth.currentUser
        return if (auth != null) {
            Log.i("TESTTT", "Current user photoUrl: " + auth.photoUrl)
            auth.photoUrl
        } else {
            null
        }
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
        startActivityForResult(
            activity, AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(), RC_SIGN_IN, null
        )
    }

    /** Sign out direct called in activity. */
    fun signOut(context: Context) {
        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener {
                SettingsActivity().restartActivity()
            }
    }


}