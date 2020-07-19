package com.txwstudio.app.roadreport.ui.account

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.txwstudio.app.roadreport.R

class AccountViewModel(application: Application) : AndroidViewModel(application) {

    var photoUrl = MutableLiveData<Uri>()
    var displayName = MutableLiveData<String>()
    var email = MutableLiveData<String>()
    var uid = MutableLiveData<String>()
    var isSignedIn = MutableLiveData<Boolean>()

    init {
        this.photoUrl.value = Uri.EMPTY
        this.isSignedIn.value = false
    }

    /**
     * Listening to auth status, called from fragment when onResume or onPause.
     * */
    val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            // User is signed in.
            photoUrl.value = firebaseUser.photoUrl
            displayName.value = firebaseUser.displayName
            email.value = firebaseUser.email
            uid.value = firebaseUser.uid
            isSignedIn.value = true
        } else {
            // User not sign in yet.
            photoUrl.value = Uri.EMPTY
            displayName.value =
                getApplication<Application>().getString(R.string.accountFragment_userNameHolder)
            email.value = ""
            uid.value = ""
            isSignedIn.value = false
        }
    }

}