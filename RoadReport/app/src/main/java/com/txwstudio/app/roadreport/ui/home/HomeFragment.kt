package com.txwstudio.app.roadreport.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.databinding.FragmentHomeBinding
import com.txwstudio.app.roadreport.handler.HomeFragClickHandler

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val binding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )

        binding.viewModel = homeViewModel
        binding.handler = context?.let { context ->
            HomeFragClickHandler(context)
        }
        binding.lifecycleOwner = this

        return binding.root
    }
}