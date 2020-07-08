package com.txwstudio.app.roadreport.adapter

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade

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

    @JvmStatic
    @BindingAdapter("imageUrls", "placeHolders", "errors")
    fun setImageUrlWithGlide(view: ImageView, url: Uri, placeHolder: Drawable, error: Drawable) {
        Glide.with(view.context)
            .load(url)
            .transition(withCrossFade())
            .placeholder(placeHolder)
            .error(error)
            .into(view)
    }
}