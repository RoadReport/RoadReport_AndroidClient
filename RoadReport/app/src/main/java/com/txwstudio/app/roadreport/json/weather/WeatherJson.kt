package com.txwstudio.app.roadreport.json.weather

import android.util.Log
import com.google.gson.annotations.SerializedName

data class WeatherJson(
    @SerializedName("records")
    var records: Records?,
    @SerializedName("result")
    var result: Result?,
    @SerializedName("success")
    var success: String?
) {

    fun getStationId(): String? {
        Log.i(
            "WeatherLog",
            "WeatherJson.getStationId().測站編號：" + records?.location?.get(0)?.stationId
        )
        return records?.location?.get(0)?.stationId
    }

    fun getLocationName(): String? {
        Log.i(
            "WeatherLog",
            "WeatherJson.getLocationName().地點：" + records?.location?.get(0)?.locationName
        )
        return records?.location?.get(0)?.locationName
    }

    fun getElementValue(): String? {
        Log.i(
            "WeatherLog",
            "WeatherJson.getElementValue().數值，" +
                    records?.location?.get(0)?.weatherElement?.get(0)?.elementName +
                    " 為：" +
                    records?.location?.get(0)?.weatherElement?.get(0)?.elementValue
        )
        return records?.location?.get(0)?.weatherElement?.get(0)?.elementValue
    }

}