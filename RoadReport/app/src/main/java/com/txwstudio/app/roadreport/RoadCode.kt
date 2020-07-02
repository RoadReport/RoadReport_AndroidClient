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
     * @RoadCode FUCKYOU: 嘉義阿婆灣，幹你娘騎慢一點啦!
     **/
    companion object {
        const val ROADCODE_EMPTY = -1
        const val ROADCODE_24 = 0
        const val ROADCODE_182 = 1
        const val ROADCODE_GRANDMA = 2
        const val ROADCODE_7 = 3
        const val ROADCODE_FUCKYOU = 4
    }


    /**
     * Invoke startActivity and send current road code to targetActivity and setCurrRoadCodeToSP().
     * @param currActivity: Current Activity, just passing "this" in.
     * @param targetActivity
     * @param roadCode: The road user just selected.
     * */
    fun startActivityWithCode(currActivity: Context, targetActivity: Context, roadCode: Int) {
        if (roadCode == ROADCODE_EMPTY) return
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

    /**
     * Get current road name using the integer saved in shared preferences.
     *
     * @param context Current activity
     *
     * @return Current road name
     * */
    fun getCurrRoadName(context: Context): String {
        return when (context.getSharedPreferences("currentCode", 0)
            .getInt("currentCode", 0)) {
            ROADCODE_24 -> context.resources.getString(R.string.roadName_24)
            ROADCODE_182 -> context.resources.getString(R.string.roadName_182)
            ROADCODE_GRANDMA -> context.resources.getString(R.string.roadName_grandma)
            ROADCODE_7 -> context.resources.getString(R.string.roadName_7)
            else -> context.resources.getString(R.string.all_unknownError)
        }
    }

    fun getCurrRoadNameTitle(context: Context): String {
        return when (context.getSharedPreferences("currentCode", 0)
            .getInt("currentCode", 0)) {
            ROADCODE_24 -> "ROADCODE_24"
            ROADCODE_182 -> "ROADCODE_182"
            ROADCODE_GRANDMA -> "ROADCODE_GRANDMA"
            ROADCODE_7 -> "ROADCODE_7"
            else -> context.resources.getString(R.string.all_unknownError)
        }
    }
}