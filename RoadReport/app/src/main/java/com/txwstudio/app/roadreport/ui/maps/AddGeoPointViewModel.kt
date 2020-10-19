package com.txwstudio.app.roadreport.ui.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class AddGeoPointViewModel : ViewModel() {
    val latlng = MutableLiveData<LatLng>()

    fun setLatLng(newLatLng: LatLng) {
        latlng.value = newLatLng
    }
}