package com.txwstudio.app.roadreport.model

import com.google.firebase.Timestamp
import java.util.*

data class Accident(
    var userName: String = "",
    var userUid: String = "",
    var time: Timestamp = Timestamp(Date()),
    var situationType: Long = -1,
    var location: String = "",
    var situation: String = ""
)