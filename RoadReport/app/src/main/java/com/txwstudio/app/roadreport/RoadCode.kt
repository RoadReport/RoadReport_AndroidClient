package com.txwstudio.app.roadreport

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent

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
     * Invoke startActivity and send current road code to targetActivity and setCurrRoadCodeToSP().
     * @param currActivity: Current Activity, just passing "this" in.
     * @param targetActivity
     * @param roadCode: The road user just selected.
     * */
    fun startActivityWithCode(currActivity: Context, targetActivity: Context, roadCode: Int) {
        val intent = Intent(currActivity, targetActivity::class.java)
        intent.putExtra("ROADCODE", roadCode)
        setCurrRoadCodeToSP(currActivity, roadCode)
        currActivity.startActivity(intent)
    }


    /**
     * Create a shared preference value to save current road code.
     * @param currRoadCode: Current Road Code.
     * */
    @SuppressLint("ApplySharedPref")
    private fun setCurrRoadCodeToSP(context: Context, currRoadCode: Int) {
        context.getSharedPreferences("currentCode", 0)
            .edit().putInt("currentCode", currRoadCode)
            .commit()
    }


    fun getCurrentRoadCode(context: Context): Int {
        return context.getSharedPreferences("currentCode", 0)
            .getInt("currentCode", 0)
    }

}