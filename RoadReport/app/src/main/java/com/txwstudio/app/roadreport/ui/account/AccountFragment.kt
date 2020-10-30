package com.txwstudio.app.roadreport.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.SettingsFragment
import com.txwstudio.app.roadreport.databinding.FragmentAccountBinding
import com.txwstudio.app.roadreport.handler.AccountFragClickHandler

class AccountFragment : Fragment() {

    private lateinit var accountViewModel: AccountViewModel
    private lateinit var binding: FragmentAccountBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        accountViewModel = ViewModelProvider(this).get(AccountViewModel::class.java)

        binding = FragmentAccountBinding.inflate(inflater, container, false)

        binding.viewModel = accountViewModel
        binding.handler = context?.let { context ->
            activity?.let { activity -> AccountFragClickHandler(context, activity) }
        }
        binding.lifecycleOwner = this

        subscribeUI()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
    }

    override fun onResume() {
        super.onResume()
        FirebaseAuth.getInstance().addAuthStateListener(accountViewModel.authStateListener)
    }

    override fun onPause() {
        super.onPause()
        FirebaseAuth.getInstance().removeAuthStateListener(accountViewModel.authStateListener)
    }

    fun subscribeUI() {
        var visible = false

        accountViewModel.isSignedIn.observe(viewLifecycleOwner) {
            if (it) {
                // User is signed in.
                binding.cardViewAccountFragAccountPreviewNotSignedIn.visibility = View.GONE
                binding.cardViewAccountFragAccountPreview.visibility = View.VISIBLE
            } else {
                // User isn't signed in.
                binding.cardViewAccountFragAccountPreviewNotSignedIn.visibility = View.VISIBLE
                binding.cardViewAccountFragAccountPreview.visibility = View.GONE
                binding.cardViewAccountFragAccountDetail.visibility = View.GONE
                binding.imageViewAccountFragMore.rotation = 90f
                visible = false
            }
        }

        val rotateFromTopToBottom = RotateAnimation(
            -180f,
            0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotateFromTopToBottom.duration = 150
        rotateFromTopToBottom.fillAfter = true

        val rotateFromBottomToTop = RotateAnimation(
            0f,
            -180f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotateFromBottomToTop.duration = 150
        rotateFromBottomToTop.fillAfter = true

        binding.cardViewAccountFragAccountPreview.setOnClickListener {
            binding.cardViewAccountFragAccountDetail.visibility = if (visible) {
                binding.imageViewAccountFragMore.startAnimation(rotateFromTopToBottom)
                visible = false
                View.GONE
            } else {
                binding.imageViewAccountFragMore.startAnimation(rotateFromBottomToTop)
                visible = true
                View.VISIBLE
            }
        }

        // Open settings fragment
        binding.textViewAccountFragOpenPreferences.setOnClickListener {
            navController.navigate(R.id.action_navigation_account_to_settingsFragment)
        }

    }
}