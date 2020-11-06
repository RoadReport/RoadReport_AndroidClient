package com.txwstudio.app.roadreport.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class AccidentEventParcelize(
    var userName: String = "",
    var userUid: String = "",
    var time: Timestamp = Timestamp(Date()),
    var situationType: Long = -1,
    var locationText: String = "",
    var locationGeoPointLatitude: Double = 0.0,
    var locationGeoPointLongitude: Double = 0.0,
    var situation: String = "",
    var imageUrl: String = ""
) : Parcelable