package com.txwstudio.app.roadreport

import android.content.Context
import android.widget.Toast

class Util {

    fun toast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }


}