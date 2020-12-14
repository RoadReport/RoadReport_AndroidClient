package com.txwstudio.app.roadreport.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel() : ViewModel() {

    val selectedRoad = MutableLiveData<Int>(-1)

    fun setRoadCode(roadcode: Int) {
        selectedRoad.value = roadcode
    }
}