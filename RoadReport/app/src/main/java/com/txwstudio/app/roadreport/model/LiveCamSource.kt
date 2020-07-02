package com.txwstudio.app.roadreport.model

import com.google.gson.annotations.SerializedName

data class LiveCamSource(
    @SerializedName("camName") var camName: String,
    @SerializedName("url") var url: String
)