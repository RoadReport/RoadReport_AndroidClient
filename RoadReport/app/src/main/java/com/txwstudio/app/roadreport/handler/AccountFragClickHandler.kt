package com.txwstudio.app.roadreport.handler

import android.app.Activity
import android.content.Context
import android.view.View
import com.txwstudio.app.roadreport.firebase.AuthManager

class AccountFragClickHandler(var context: Context, var activity: Activity) {

    fun signInOut(view: View) {
        val status = AuthManager().isUserSignedIn()
        if (status) {
            // SIGNED IN, GOTO sign out.
            AuthManager().signOut(context)
            // FirebaseAuth.getInstance().signOut()
        } else if (!status) {
            // NOT SIGNED IN, GOTO sign in.
            AuthManager().signIn(activity)
        }
    }

}