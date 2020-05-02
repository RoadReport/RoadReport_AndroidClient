package com.txwstudio.app.roadreport.model

import com.google.firebase.Timestamp

data class Accident(
    var userName: String,
    var userUid: String,
    var time: Timestamp,
    var situationType: Long,
    var location: String,
    var situation: String
)