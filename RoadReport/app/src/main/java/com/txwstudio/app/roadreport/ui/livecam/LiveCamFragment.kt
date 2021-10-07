package com.txwstudio.app.roadreport.ui.livecam

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.adapter.LiveCamSelectCardAdapter
import com.txwstudio.app.roadreport.databinding.FragmentLiveCamBinding

class LiveCamFragment : Fragment() {

    companion object {
        fun newInstance() = LiveCamFragment()
    }

    private lateinit var liveCamViewModel: LiveCamViewModel
    private lateinit var binding: FragmentLiveCamBinding
    private lateinit var adapter: LiveCamSelectCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        liveCamViewModel = ViewModelProvider(this).get(LiveCamViewModel::class.java)

        binding = FragmentLiveCamBinding.inflate(inflater, container, false)

        binding.viewModel = liveCamViewModel
        binding.lifecycleOwner = this

        adapter = LiveCamSelectCardAdapter()
        binding.recyclerViewLiveCamFrag.adapter = adapter
        subscribeUI(adapter)

        binding.webViewLivaCamFrag.setBackgroundColor(Color.BLACK)

        val adRequest = AdRequest.Builder().build()
        binding.adViewLiveCamFrag.loadAd(adRequest)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        liveCamViewModel.getLiveCamSourceListAndSetupLiveCamCard()
    }

    override fun onPause() {
        adapter.camNames.value = getString(R.string.liveCamFrag_noCamSelectTitleHolder)
        adapter.streamUrls.value = "about:blank"
        super.onPause()
    }

    private fun subscribeUI(adapter: LiveCamSelectCardAdapter) {
        liveCamViewModel.liveCamSourcesList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        adapter.camNames.observe(viewLifecycleOwner) {
            // Remove cam name display after commit a812bc5
//            binding.textViewLivaCamFrag.text = it
        }

        adapter.streamUrls.observe(viewLifecycleOwner) {
            binding.webViewLivaCamFrag.loadUrl(it)
        }
    }
}