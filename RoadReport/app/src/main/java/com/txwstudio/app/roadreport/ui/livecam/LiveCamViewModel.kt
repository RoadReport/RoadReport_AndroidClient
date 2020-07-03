package com.txwstudio.app.roadreport.ui.livecam

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.GsonBuilder
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.model.LiveCamSource

class LiveCamViewModel(application: Application) : AndroidViewModel(application) {

    var camName = MutableLiveData<String>()
    var streamUrl = MutableLiveData<String>()
//    var liveCamSourcesList = arrayOf<LiveCamSource>()
    var liveCamSourcesList = MutableLiveData<MutableList<LiveCamSource>>()
    var isRefreshing = MutableLiveData<Boolean>()

    /**
     * Fetch weather station id from Firebase Remote Config,
     * use to dynamic update the stations we used.
     * */
    fun getLiveCamSourceListAndSetupLiveCamCard() {
        isRefreshing.value = true
        val currentRoadNameTitle = RoadCode().getCurrRoadDynamicLiveCamSourceTitle(getApplication())

        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        })
        remoteConfig.fetchAndActivate().addOnSuccessListener {
            putIdToList(remoteConfig[currentRoadNameTitle].asString())
        }.addOnFailureListener {
            putIdToList("[{\"camName\":\"FAILURE\",\"url\":\"FAILURE\"}]")
        }
    }

    /**
     * Invoke by getLiveCamSourceListAndSetupLiveCamCard().
     * Turn the normal string into json and save it in the array.
     *
     * @param stringFromRemoteConfig The string for convert.
     * */
    private fun putIdToList(stringFromRemoteConfig: String) {
        val magicConverter = GsonBuilder().create()
            .fromJson(stringFromRemoteConfig, Array<LiveCamSource>::class.java)
        liveCamSourcesList.value = magicConverter.toMutableList()
//        getWeatherDataUsingCoroutine()
        liveCamSourcesList.value?.forEach {
            Log.i("LiveCamLog", "目前從 Remote Config 中取得的數值為: ${it.camName} with ${it.url}")
        }
    }

    fun seyHello(view: View, liveCamSource: LiveCamSource) {

        Log.i("LiveCamLog", "${liveCamSource.camName} with ${liveCamSource.url}")

    }

}