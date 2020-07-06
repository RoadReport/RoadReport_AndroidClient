package com.txwstudio.app.roadreport.json

import com.google.gson.annotations.SerializedName

data class DynamicWeatherStation(
    @SerializedName("stationName") val stationName: String,
    @SerializedName("stationId") val stationId: String
)