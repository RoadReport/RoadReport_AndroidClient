package com.txwstudio.app.roadreport

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Road code here
 * TODO: Consider moving these to a independent file.
 * @code ROADCODE_24: 台24 霧台
 * @code ROADCODE_182: 182 縣道
 * @code ROADCODE_GRANDMA: 嘉義阿婆灣
 * @code ROADCODE_7: 台7乙，摔車聖地
 * */
val ROADCODE_24 = 0
val ROADCODE_182 = 1
val ROADCODE_GRANDMA = 2
val ROADCODE_7 = 3

class RoadCode {

    //TODO: Find another way to store these global variable.
    /**
     * Save road code in Interface for now.
     *
     *
    //    val ROADCODE_24 = 0
    //    val ROADCODE_182 = 1
    //    val ROADCODE_GRANDMA = 2
    //    val ROADCODE_7 = 3


    companion object Blah {
    val ee = 0
    }
     */


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