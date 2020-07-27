package com.txwstudio.app.roadreport.ui.weather

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.GsonBuilder
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.json.DynamicWeatherStation
import com.txwstudio.app.roadreport.model.WeatherData
import com.txwstudio.app.roadreport.service.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    var isRefreshing = MutableLiveData<Boolean>()

    private var dynamicStations = arrayOf<DynamicWeatherStation>()
    var weatherDataList = MutableLiveData<MutableList<WeatherData>>()

    /**
     * Fetch weather station id from Firebase Remote Config,
     * use to dynamic update the stations we used.
     * */
    fun getWeatherStationListAndSetupWeatherCard() {
        isRefreshing.value = true
        val currentRoadNameTitle = RoadCode().getCurrRoadNameTitle(getApplication())

        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        })
        remoteConfig.fetchAndActivate().addOnSuccessListener {
            putIdToList(remoteConfig[currentRoadNameTitle].asString())
        }.addOnFailureListener {
            putIdToList("[{\"station\":\"FAILURE\"}]")
        }
    }

    /**
     * Invoke by getWeatherStationListAndSetupWeatherCard().
     * Turn the normal string into json and save it in the array.
     *
     * @param stringFromRemoteConfig The string for convert.
     * */
    private fun putIdToList(stringFromRemoteConfig: String) {
        val magicConverter = GsonBuilder().create()
            .fromJson(stringFromRemoteConfig, Array<DynamicWeatherStation>::class.java)
        dynamicStations = magicConverter
        getWeatherDataUsingCoroutine()
    }

    /**
     * Invoke by putIdToList().
     * Using the stationIds array to fetch weather data from government's opendata platform.
     * Using retrofit by Sync method in Kotlin coroutine.
     * */
    private fun getWeatherDataUsingCoroutine() {
        isRefreshing.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val listToAdd = mutableListOf<WeatherData>()
            for (station in dynamicStations) {
                val apiTemp = WeatherApi.retrofitService.getWeatherTemp(station.stationId)
                val apiHum = WeatherApi.retrofitService.getWeatherHum(station.stationId)

                val temp = apiTemp.execute().body()
                val hum = apiHum.execute().body()

                var tempElementValue: String?
                var humElementValue: String?

                tempElementValue = if (!temp?.isLocationEmpty()!!) {
                    if (!temp.isElementEmpty()!!) {
                        temp.getElementValue()?.substring(0, 2)
                    } else {
                        "--"
                    }
                } else {
                    "--"
                }

                humElementValue = if (!hum?.isLocationEmpty()!!) {
                    if (!hum.isElementEmpty()!!) {
                        hum.getElementValue()
                    } else {
                        "--"
                    }
                } else {
                    "--"
                }

                listToAdd.add(
                    WeatherData(
                        station.stationName,
                        station.stationId,
                        tempElementValue,
                        humElementValue
                    )
                )

            } // .for loop
            weatherDataList.postValue(listToAdd)
            isRefreshing.postValue(false)
        } // .viewModelScope
    }
}