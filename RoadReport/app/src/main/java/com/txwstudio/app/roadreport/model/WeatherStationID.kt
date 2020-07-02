package com.txwstudio.app.roadreport.model

import com.google.gson.annotations.SerializedName

data class WeatherStationID(
    @SerializedName("station")
    val station: String
)