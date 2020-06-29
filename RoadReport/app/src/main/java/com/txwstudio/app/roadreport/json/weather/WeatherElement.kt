package com.txwstudio.app.roadreport.json.weather

import com.google.gson.annotations.SerializedName

data class WeatherElement(
    @SerializedName("elementName")
    var elementName: String?,
    @SerializedName("elementValue")
    var elementValue: String?
)