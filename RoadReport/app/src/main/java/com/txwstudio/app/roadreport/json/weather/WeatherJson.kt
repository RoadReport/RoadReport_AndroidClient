package com.txwstudio.app.roadreport.json.weather

import com.google.gson.annotations.SerializedName

data class WeatherJson(
    @SerializedName("records")
    var records: Records?,
    @SerializedName("result")
    var result: Result?,
    @SerializedName("success")
    var success: String?
) {

    fun getElementValue(): String? {
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