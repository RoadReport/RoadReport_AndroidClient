package com.txwstudio.app.roadreport.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.RoadCode
import com.txwstudio.app.roadreport.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    private lateinit var navController: NavController

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
    }

    private fun subscribeUI() {
        binding.card0.setOnClickListener {
            RoadCode().setCurrRoadCodeToSP(requireContext(), RoadCode.ROADCODE_24)
            navController.navigate(R.id.action_navigation_home_to_roadActivity)
        }

        binding.card1.setOnClickListener {
            RoadCode().setCurrRoadCodeToSP(requireContext(), RoadCode.ROADCODE_182)
            navController.navigate(R.id.action_navigation_home_to_roadActivity)
        }
    }
}