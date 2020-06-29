package com.txwstudio.app.roadreport.json.weather

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("lat")
    var lat: String?,
    @SerializedName("locationName")
    var locationName: String?,
    @SerializedName("lon")
    var lon: String?,
    @SerializedName("parameter")
    var parameter: List<Parameter>?,
    @SerializedName("stationId")
    var stationId: String?,
    @SerializedName("time")
    var time: Time?,
    @SerializedName("weatherElement")
    var weatherElement: List<WeatherElement>?
)