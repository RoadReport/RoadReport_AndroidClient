package com.txwstudio.app.roadreport.ui.livecam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
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

        binding = DataBindingUtil.inflate<FragmentLiveCamBinding>(
            inflater,
            R.layout.fragment_live_cam,
            container,
            false
        )
        binding.viewModel = liveCamViewModel
        binding.lifecycleOwner = this

        adapter = LiveCamSelectCardAdapter()
        binding.recyclerViewLiveCamFrag.adapter = adapter
        subscribeUI(adapter)

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

    fun subscribeUI(adapter: LiveCamSelectCardAdapter) {
        liveCamViewModel.liveCamSourcesList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        adapter.camNames.observe(viewLifecycleOwner) {
            binding.textViewLivaCamFrag.text = it
        }

        adapter.streamUrls.observe(viewLifecycleOwner) {
            binding.webViewLivaCamFrag.loadUrl(it)
        }
    }
}