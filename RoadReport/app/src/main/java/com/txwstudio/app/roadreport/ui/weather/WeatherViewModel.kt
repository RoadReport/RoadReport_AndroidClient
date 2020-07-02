package com.txwstudio.app.roadreport.ui.weather

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.txwstudio.app.roadreport.json.weather.WeatherJson
import com.txwstudio.app.roadreport.model.WeatherData
import com.txwstudio.app.roadreport.service.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel : ViewModel() {

    var locationToShow = MutableLiveData<String>()
    var tempToShow = MutableLiveData<String>()
    var humidityToShow = MutableLiveData<String>()
    var isRefreshing = MutableLiveData<Boolean>()

    private val stationId = arrayListOf("C0R130", "C0R150")
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
     * Using retrofit by Sync method in Kotlin coroutine.
     *
     * TODO(Error Handling)
     * */
    fun getWeatherDataUsingCoroutine() {
        isRefreshing.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val listToAdd = mutableListOf<WeatherData>()
            for (stationId in stationId) {
                val apiTemp = WeatherApi.retrofitService.getWeatherTemp(stationId)
                val apiHum = WeatherApi.retrofitService.getWeatherHum(stationId)

                val temp = apiTemp.execute().body()
                val hum = apiHum.execute().body()

                if (!temp?.isElementExist()!! || !hum?.isElementExist()!!) {
                    listToAdd.add(
                        WeatherData(
                            "請稍後在試",
                            "--",
                            "--",
                            "--"
                        )
                    )
                    break
                }

                listToAdd.add(
                    WeatherData(
                        temp?.getLocationName(),
                        temp?.getStationId(),
                        temp?.getElementValue(),
                        hum?.getElementValue()
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


}