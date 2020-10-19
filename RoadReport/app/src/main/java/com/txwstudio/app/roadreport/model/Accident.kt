package com.txwstudio.app.roadreport.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*

@Parcelize
data class Accident(
    var userName: String = "",
    var userUid: String = "",
    var time: Timestamp = Timestamp(Date()),
    var situationType: Long = -1,
    var location: String = "",
    var geoPoint: @RawValue GeoPoint = GeoPoint(0.0, 0.0),
    var situation: String = "",
    var imageUrl: String = ""
) : Parcelable