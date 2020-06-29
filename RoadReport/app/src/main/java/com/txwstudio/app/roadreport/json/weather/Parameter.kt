package com.txwstudio.app.roadreport.json.weather

import com.google.gson.annotations.SerializedName

data class Parameter(
    @SerializedName("parameterName")
    var parameterName: String?,
    @SerializedName("parameterValue")
    var parameterValue: String?
)