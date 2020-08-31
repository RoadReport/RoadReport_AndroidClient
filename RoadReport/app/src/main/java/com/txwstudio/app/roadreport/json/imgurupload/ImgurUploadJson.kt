package com.txwstudio.app.roadreport.json.imgurupload

import com.google.gson.annotations.SerializedName

data class ImgurUploadJson(
    @SerializedName("data") val data: Data?,
    @SerializedName("status") val status: Int?,
    @SerializedName("success") val success: Boolean?
)