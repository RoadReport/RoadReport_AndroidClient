package com.txwstudio.app.roadreport.json.weather

import com.google.gson.annotations.SerializedName

data class Field(
    @SerializedName("id")
    var id: String?,
    @SerializedName("type")
    var type: String?
)