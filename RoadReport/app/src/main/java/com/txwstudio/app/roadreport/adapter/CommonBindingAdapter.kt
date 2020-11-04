package com.txwstudio.app.roadreport.adapter

import android.view.View
import androidx.databinding.BindingAdapter

object CommonBindingAdapter {
    @JvmStatic
    @BindingAdapter("isGone")
    fun setVisibility(view: View, isGone: Boolean) {
        if (isGone) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    }
}