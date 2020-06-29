package com.txwstudio.app.roadreport.json.weather

import com.google.gson.annotations.SerializedName

data class Time(
    @SerializedName("obsTime")
    var obsTime: String?
)