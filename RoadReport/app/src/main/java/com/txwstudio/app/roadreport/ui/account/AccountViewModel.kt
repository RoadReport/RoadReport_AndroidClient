package com.txwstudio.app.roadreport.ui.account

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.txwstudio.app.roadreport.R

class AccountViewModel(application: Application) : AndroidViewModel(application) {

    var displayName = MutableLiveData<String>()
    var photoUrl = MutableLiveData<Uri>()
    var signInStatus = MutableLiveData<String>()

    init {
        this.photoUrl.value = Uri.EMPTY
    }

    /**
     * Listening to auth status, called from fragment when onResume or onPause.
     * */
    val authStateListener = FirebaseAuth.AuthStateListener {firebaseAuth ->
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            // User is signed in.
            this.displayName.value = firebaseUser.displayName
            this.photoUrl.value = firebaseUser.photoUrl
            this.signInStatus.value = getApplication<Application>().getString(R.string.accountFragment_accountStatusSignIn)
        } else {
            // User not sign in yet.
            this.displayName.value = getApplication<Application>().getString(R.string.accountFragment_userNameHolder)
            this.photoUrl.value = Uri.EMPTY
            this.signInStatus.value = getApplication<Application>().getString(R.string.accountFragment_accountStatusSignedOut)
        }
    }

}