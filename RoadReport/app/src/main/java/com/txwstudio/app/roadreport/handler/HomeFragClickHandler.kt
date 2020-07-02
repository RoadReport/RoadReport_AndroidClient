package com.txwstudio.app.roadreport.handler

import android.content.Context
import android.view.View
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.activity.RoadActivity2

class HomeFragClickHandler(var context: Context) {

    fun gotoRoadActivity(view: View) {
        var roadCode = RoadCode.ROADCODE_EMPTY
        when (view.id) {
            R.id.card_0 -> roadCode = RoadCode.ROADCODE_24
            R.id.card_1 -> roadCode = RoadCode.ROADCODE_182
        }
        RoadCode().startActivityWithCode(context, RoadActivity2(), roadCode)
    }

}