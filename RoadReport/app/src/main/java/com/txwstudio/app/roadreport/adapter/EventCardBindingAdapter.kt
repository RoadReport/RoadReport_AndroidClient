package com.txwstudio.app.roadreport.adapter

import android.graphics.Paint
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.txwstudio.app.roadreport.R

object EventCardBindingAdapter {
    @JvmStatic
    @BindingAdapter("situationType")
    fun setEventCardBackground(view: RelativeLayout, situationType: Long) {
        val resId = when (situationType.toInt()) {
            1, 2 -> R.drawable.bg_accident_type_2
            3, 4 -> R.drawable.bg_accident_type_1
            5 -> R.drawable.bg_accident_type_4
            6 -> R.drawable.bg_accident_type_5
            else -> R.drawable.bg_accident_type_5
        }
        view.background = ContextCompat.getDrawable(view.context, resId)
    }

    @JvmStatic
    @BindingAdapter("situationType")
    fun setEventCardSituationText(view: TextView, situationType: Long) {
        val resId = when (situationType.toInt()) {
            1 -> R.string.accidentEvent_situationType_1
            2 -> R.string.accidentEvent_situationType_2
            3 -> R.string.accidentEvent_situationType_3
            4 -> R.string.accidentEvent_situationType_4
            5 -> R.string.accidentEvent_situationType_5
            6 -> R.string.accidentEvent_situationType_6
            else -> R.string.accidentEvent_situationType_6
        }
        view.text = view.context.getString(resId)
    }

    @JvmStatic
    @BindingAdapter("isLocationGeoSet")
    fun setEventCardLocationTextUnderLine(view: TextView, isLocationGeoSet: Boolean) {
        if (isLocationGeoSet) {
            view.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        } else {
            view.paintFlags = 0
        }
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setEventCardImage(view: ImageView, imageUrl: String) {
        if (!imageUrl.isBlank()) {
            Glide.with(view.context)
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
        }
    }
}