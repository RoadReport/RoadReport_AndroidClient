package com.txwstudio.app.roadreport.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.activity.RoadActivity
import com.txwstudio.app.roadreport.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.viewModel = homeViewModel
        binding.lifecycleOwner = this

        subscribeUI()

        return binding.root
    }

    private fun subscribeUI() {
        binding.card0.setOnClickListener {
            RoadCode().startActivityWithCode(requireContext(), RoadActivity(), RoadCode.ROADCODE_24)
        }

        binding.card1.setOnClickListener {
            RoadCode().startActivityWithCode(requireContext(), RoadActivity(), RoadCode.ROADCODE_182)
        }
    }
}