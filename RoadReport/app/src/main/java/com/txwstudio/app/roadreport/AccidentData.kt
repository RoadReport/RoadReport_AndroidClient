package com.txwstudio.app.roadreport

import com.google.firebase.Timestamp

data class AccidentData(
    var userName: String,
    var userUid: String,
    var time: Timestamp,
    var situationType: Long,
    var location: String,
    var situation: String
)