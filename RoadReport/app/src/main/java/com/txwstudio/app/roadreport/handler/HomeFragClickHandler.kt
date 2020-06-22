package com.txwstudio.app.roadreport.handler

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.activity.RoadActivity

class HomeFragClickHandler(var context: Context, var activity: Activity) {

    fun gotoRoadActivity(view: View) {
        var roadCode = RoadCode.ROADCODE_EMPTY
        when (view.id) {
            R.id.card_0 -> roadCode = RoadCode.ROADCODE_24
            R.id.card_1 -> roadCode = RoadCode.ROADCODE_182
        }
        RoadCode().startActivityWithCode(context, RoadActivity(), roadCode)
    }

}