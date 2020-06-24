package com.txwstudio.app.roadreport.handler

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.firebase.AuthManager

class AccountFragClickHandler(var context: Context, var activity: Activity) {

    fun signInOut(view: View) {
        val status = AuthManager().userIsSignedIn()
        if (status) {
            // SIGNED IN, GOTO sign out.
            AuthManager().signOut(context)
            // FirebaseAuth.getInstance().signOut()
        } else if (!status) {
            // NOT SIGNED IN, GOTO sign in.
            AuthManager().signIn(activity)
        }
    }

    fun aboutLink(view: View) {
        var url = "https://roadreport.gitlab.io/docs"
        when (view.id) {
            R.id.textView_settings_termOfServiceLink -> url =
                "https://bit.ly/RoadRTos"
            R.id.textView_settings_privacyPolicyLink -> url =
                "https://bit.ly/RoadRPp"
            R.id.textView_settings_licenseLink -> url =
                "https://bit.ly/RoadRLicense"
            R.id.textView_settings_aboutLink -> url =
                "https://bit.ly/RoadRAbout"
        }
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }
}