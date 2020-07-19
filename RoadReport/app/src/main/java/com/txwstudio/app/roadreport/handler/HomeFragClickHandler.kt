package com.txwstudio.app.roadreport.handler

import android.content.Context
import android.view.View
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.activity.RoadActivity

class HomeFragClickHandler(var context: Context) {

    fun gotoRoadActivity(view: View) {
        val roadCode = when (view.id) {
            R.id.card_0 -> RoadCode.ROADCODE_24
            R.id.card_1 -> RoadCode.ROADCODE_182
            else -> RoadCode.ROADCODE_EMPTY
        }
        RoadCode().startActivityWithCode(context, RoadActivity(), roadCode)
    }

}