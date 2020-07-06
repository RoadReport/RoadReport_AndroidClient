package com.txwstudio.app.roadreport.json

import com.google.gson.annotations.SerializedName

data class DynamicLiveCamSource(
    @SerializedName("camName") var camName: String,
    @SerializedName("url") var url: String
)