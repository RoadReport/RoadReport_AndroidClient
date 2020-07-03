package com.txwstudio.app.roadreport.ui.livecam

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.adapter.LiveCamSelectCardAdapter
import com.txwstudio.app.roadreport.adapter.LiveCamSelectCardAdapter.Companion.camName
import com.txwstudio.app.roadreport.adapter.LiveCamSelectCardAdapter.Companion.streamUrl
import com.txwstudio.app.roadreport.databinding.FragmentLiveCamBinding

class LiveCamFragment : Fragment() {

    companion object {
        fun newInstance() = LiveCamFragment()
    }

    private lateinit var liveCamViewModel: LiveCamViewModel
    private lateinit var binding: FragmentLiveCamBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        liveCamViewModel = ViewModelProvider(this).get(LiveCamViewModel::class.java)

        binding = DataBindingUtil.inflate<FragmentLiveCamBinding>(
            inflater,
            R.layout.fragment_live_cam,
            container,
            false
        )
        binding.viewModel = liveCamViewModel
        binding.lifecycleOwner = this

        val adapter = LiveCamSelectCardAdapter()
        binding.recyclerViewLiveCamFrag.adapter = adapter
        subscribeUI(adapter)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        liveCamViewModel.getLiveCamSourceListAndSetupLiveCamCard()
    }

    override fun onPause() {
        super.onPause()
        binding.webViewLivaCamFrag.loadUrl("about:blank")
    }

    fun subscribeUI(adapter: LiveCamSelectCardAdapter) {

        liveCamViewModel.liveCamSourcesList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        camName.observe(viewLifecycleOwner){
            Log.i("LiveCamLog", "The name is fucking changing")
            binding.textViewLivaCamFrag.text = it
        }

        streamUrl.observe(viewLifecycleOwner) {
            Log.i("LiveCamLog", "The url is fucking changing")
            binding.webViewLivaCamFrag.loadUrl(it)
        }
    }
}