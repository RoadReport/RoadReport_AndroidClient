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

    fun getLocationName(): String? {
        Log.i(
            "WeatherLog",
            "WeatherJson.getLocationName().地點：" + records?.location?.get(0)?.locationName
        )
        return records?.location?.get(0)?.locationName
    }

    fun getStationId(): String? {
        Log.i(
            "WeatherLog",
            "WeatherJson.getStationId().測站編號：" + records?.location?.get(0)?.stationId
        )
        return records?.location?.get(0)?.stationId
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

    /**
     * Check for weatherElement do exist.
     * I guess when they update the value, the weatherElement will be empty for a second.
     *
     * @return True, WeatherElement IS EMPTY.
     * @return False, WeatherElement IS NOT EMPTY.
     * */
    fun isLocationEmpty(): Boolean? {
        return records?.location?.isEmpty()
    }

    fun isElementEmpty(): Boolean? {
        return records?.location?.get(0)?.weatherElement?.isEmpty()
    }
}