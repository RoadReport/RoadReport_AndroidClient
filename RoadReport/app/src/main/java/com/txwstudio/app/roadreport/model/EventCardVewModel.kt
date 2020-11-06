package com.txwstudio.app.roadreport.model

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.txwstudio.app.roadreport.firebase.AuthManager
import java.text.SimpleDateFormat
import java.util.*

class EventCardVewModel(events: Accident) {
    private val event = events
    private val currentUserUid = AuthManager().getCurrUserModel()?.uid

    var situationType = event.situationType

    var locationText = event.locationText

    var isLocationGeoSet = events.locationGeoPoint != GeoPoint(0.0, 0.0)

    var locationGeoPoint: LatLng? =
        if (events.locationGeoPoint == GeoPoint(0.0, 0.0)) null
        else LatLng(event.locationGeoPoint.latitude, event.locationGeoPoint.longitude)

    var isUserSignedIn = AuthManager().isUserSignedIn()

    var situation = event.situation

    var imageUrl = event.imageUrl

    var isImageSet = !event.imageUrl.isBlank()

    val time: String
        get() = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(event.time.toDate())

    var userName = event.userName
}