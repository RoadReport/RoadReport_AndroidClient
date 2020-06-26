package com.txwstudio.app.roadreport.ui.livecam

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.txwstudio.app.roadreport.R

class LiveCamFragment : Fragment() {

    companion object {
        fun newInstance() = LiveCamFragment()
    }

    private lateinit var viewModel: LiveCamViewModel
    private lateinit var mWebView: WebView

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_live_cam, container, false)

        mWebView = root.findViewById<WebView>(R.id.webView_LivaCamFrag)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LiveCamViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onResume() {
        super.onResume()
        mWebView.loadUrl("https://thbcctv11.thb.gov.tw/T24-26K+700")
    }

    override fun onStop() {
        super.onStop()
    }
}