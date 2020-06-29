package com.txwstudio.app.roadreport.json.weather

import com.google.gson.annotations.SerializedName

data class Records(
    @SerializedName("location")
    var location: List<Location>?
)