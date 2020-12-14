package com.txwstudio.app.roadreport.ui.imageviewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.txwstudio.app.roadreport.R
import kotlinx.android.synthetic.main.fragment_image_viewer.*

private const val ARG_IMAGE_URL = "imageUrl"

class ImageViewerFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance(imageUrl: String) =
            ImageViewerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_IMAGE_URL, imageUrl)
                }
            }
    }

    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUrl = it.getString(ARG_IMAGE_URL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireContext())
            .load(imageUrl)
            .into(touchImageView_imageViewerFrag)
    }
}