package com.txwstudio.app.roadreport.activity

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.Util
import kotlinx.android.synthetic.main.activity_image_viewer.*

class ImageViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Util().setupTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)
        setSupportActionBar(toolbar_imageViewer)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_activity_imageViewer)

        Glide.with(baseContext)
            .load(intent.getStringExtra("imageUrl"))
            .into(touchImageView_imageViewer)
    }

}