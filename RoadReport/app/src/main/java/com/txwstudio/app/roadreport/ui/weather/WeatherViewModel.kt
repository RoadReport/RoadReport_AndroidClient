package com.txwstudio.app.roadreport.ui.weather

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.GsonBuilder
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.json.weather.WeatherJson
import com.txwstudio.app.roadreport.json.DynamicWeatherStation
import com.txwstudio.app.roadreport.model.WeatherData
import com.txwstudio.app.roadreport.service.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    var locationToShow = MutableLiveData<String>()
    var tempToShow = MutableLiveData<String>()
    var humidityToShow = MutableLiveData<String>()
    var isRefreshing = MutableLiveData<Boolean>()

    private var dynamicStations = arrayOf<DynamicWeatherStation>()
    var weatherDataList = MutableLiveData<MutableList<WeatherData>>()

    /**
     *  Using retrofit by Async method.
     *  Remove these code soon.
     *  */
    fun getWeatherDataBasic() {
        val api = WeatherApi.retrofitService.getWeatherTemp("C0R130")
        val api2 = WeatherApi.retrofitService.getWeatherHum("C0R130")

        api.enqueue(object : Callback<WeatherJson> {
            override fun onResponse(call: Call<WeatherJson>, response: Response<WeatherJson>) {
                locationToShow.value = response.body()?.getLocationName()
                tempToShow.value = response.body()?.getElementValue()
                Log.i(
                    "WeatherLog",
                    "取得 O-A0001-001 資料：" +
                            "${response.body()?.getLocationName()}, " +
                            "${response.body()?.getElementValue()}"
                )
            }

            override fun onFailure(call: Call<WeatherJson>, t: Throwable) {
                Log.w("WeatherLog", "取得 O-A0001-001 資料失敗：" + t.message)
            }
        })
        api2.enqueue(object : Callback<WeatherJson> {
            override fun onResponse(call: Call<WeatherJson>, response: Response<WeatherJson>) {
                humidityToShow.value = response.body()?.getElementValue()
                Log.i(
                    "WeatherLog",
                    "取得 O-A0002-001 資料：${response.body()?.getElementValue()}"
                )
            }

            override fun onFailure(call: Call<WeatherJson>, t: Throwable) {
                Log.w("WeatherLog", "取得 O-A0002-001 資料失敗：" + t.message)
            }
        })
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
            for (item in listToAdd) {
                Log.i("WeatherLog", "${item.locationName} 現在 ${item.tempValue} 度")
            }
            weatherDataList.postValue(listToAdd)
            isRefreshing.postValue(false)
        } // .viewModelScope
    }

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
        for (item in magicConverter) {
            Log.i(
                "WeatherLog",
                "目前從 Remote Config 中取得的數值為: ${item.stationName} - ${item.stationId}"
            )
        }
    }
}