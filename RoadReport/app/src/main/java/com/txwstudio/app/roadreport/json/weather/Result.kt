package com.txwstudio.app.roadreport.json.weather

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("fields")
    var fields: List<Field>?,
    @SerializedName("resource_id")
    var resourceId: String?
)