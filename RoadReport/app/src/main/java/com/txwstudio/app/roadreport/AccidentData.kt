package com.txwstudio.app.roadreport

import com.google.firebase.Timestamp
import java.util.*

data class AccidentData(var situationType: Int,
                        var location: String,
                        var situation: String,
                        var userUid: String,
                        var time: Timestamp
)