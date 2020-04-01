package com.txwstudio.app.roadreport

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class RoadCode {

    /**
     * Road code inside.
     * @RoadCode 24: 台24 霧台
     * @RoadCode 182: 182 縣道
     * @RoadCode GRANDMA: 嘉義阿婆灣
     * @RoadCode 7: 台7乙，摔車聖地
     **/
    companion object {
        const val ROADCODE_24 = 0
        const val ROADCODE_182 = 1
        const val ROADCODE_GRANDMA = 2
        const val ROADCODE_7 = 3
    }


    /**
     * This fun will start activity and send the selected road to "setCurrentRoadCode" below.
     * @param currentActivity
     * @param targetActivity
     * @param roadcode: The road user just selected.
     * */
    fun startActivityByCode(currentActivity: Context, targetActivity: Context, roadcode: Int) {
        val intent = Intent(currentActivity, targetActivity::class.java)
        intent.putExtra("ROADCODE", roadcode)
        setCurrentRoadCode(currentActivity, roadcode)
        currentActivity.startActivity(intent)
    }

    /**
     * Create a shared preference value to save current road code.
     * @param currentRoadCode
     * */
    fun setCurrentRoadCode(context: Context, currentRoadCode: Int) {
        val currentCode: SharedPreferences = context.getSharedPreferences("currentCode", 0)
        currentCode.edit().putInt("currentCode", currentRoadCode).commit()
    }


    fun getCurrentRoadCode(context: Context): Int {
        return context.getSharedPreferences("currentCode", 0)
            .getInt("currentCode", 0)
    }

}