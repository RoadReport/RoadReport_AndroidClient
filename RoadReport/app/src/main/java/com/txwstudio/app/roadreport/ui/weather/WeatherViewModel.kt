package com.txwstudio.app.roadreport.ui.weather

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.txwstudio.app.roadreport.json.weather.WeatherJson
import com.txwstudio.app.roadreport.model.WeatherData
import com.txwstudio.app.roadreport.service.WeatherApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel : ViewModel() {

    var locationToShow = MutableLiveData<String>()
    var tempToShow = MutableLiveData<String>()
    var humidityToShow = MutableLiveData<String>()

    private val stationId = arrayListOf("C0R130", "C0R150")
    private val weatherDataList = arrayListOf<WeatherData>()

    fun getWeatherTempData() {
        val api = WeatherApi.retrofitService.getWeatherTemp("C0R130")
        val api2 = WeatherApi.retrofitService.getWeatherHum("C0R130")

        api.enqueue(object : Callback<WeatherJson> {
            override fun onResponse(call: Call<WeatherJson>, response: Response<WeatherJson>) {
                locationToShow.value = response.body()?.getLocationName()
                tempToShow.value = response.body()?.getElementValue()
            }

            override fun onFailure(call: Call<WeatherJson>, t: Throwable) {
                Log.i("TESTTT", "Failed :" + t.message)
            }
        })
        api2.enqueue(object : Callback<WeatherJson> {
            override fun onResponse(call: Call<WeatherJson>, response: Response<WeatherJson>) {
                humidityToShow.value = response.body()?.getElementValue()
            }

            override fun onFailure(call: Call<WeatherJson>, t: Throwable) {
                Log.i("TESTTT", "Failed :" + t.message)
            }
        })


        /** 測試中 */
        for (stationId in stationId) {
            Log.i("TESTTT", "正在取得測站ID：$stationId")
            var model = WeatherData("", "", "", "")

            val apiTemp = WeatherApi.retrofitService.getWeatherTemp(stationId)
            val apiHum = WeatherApi.retrofitService.getWeatherHum(stationId)

            apiTemp.enqueue(object : Callback<WeatherJson> {
                override fun onResponse(call: Call<WeatherJson>, response: Response<WeatherJson>) {
                    model.locationName = response.body()?.getLocationName()
                    model.stationId = response.body()?.getStationId()
                    model.tempValue = response.body()?.getElementValue()
                }

                override fun onFailure(call: Call<WeatherJson>, t: Throwable) {
                    Log.i("TESTTT", "Failed :" + t.message)
                }
            })

//            apiHum.enqueue(object : Callback<WeatherJson> {
//                override fun onResponse(call: Call<WeatherJson>, response: Response<WeatherJson>) {
//                    model.humidityValue = response.body()?.getElementValue()
//                }
//
//                override fun onFailure(call: Call<WeatherJson>, t: Throwable) {
//                    Log.i("TESTTT", "Failed :" + t.message)
//                }
//            })

//            weatherDataList.add(model)
        }


    }
}