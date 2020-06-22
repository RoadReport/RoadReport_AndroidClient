package com.txwstudio.app.roadreport.adapter

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

object ImageBindingAdapter {
    @JvmStatic
    @BindingAdapter("imageUrl", "placeHolder", "error")
    fun setImageUrl(view: ImageView, url: Uri, placeHolder: Drawable, error: Drawable) {
        Picasso.get()
            .load(url)
            .placeholder(placeHolder)
            .error(error)
            .into(view)
    }
}