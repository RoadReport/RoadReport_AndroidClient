package com.txwstudio.app.roadreport.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.firebase.auth.FirebaseAuth
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.databinding.FragmentAccountBinding
import com.txwstudio.app.roadreport.handler.AccountFragClickHandler

class AccountFragment : Fragment() {

    private lateinit var accountViewModel: AccountViewModel
    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        accountViewModel = ViewModelProvider(this).get(AccountViewModel::class.java)

        binding = DataBindingUtil.inflate<FragmentAccountBinding>(
            inflater,
            R.layout.fragment_account,
            container,
            false
        )

        binding.viewModel = accountViewModel
        binding.handler = context?.let { context ->
            activity?.let { activity -> AccountFragClickHandler(context, activity) }
        }
        binding.lifecycleOwner = this

        subscribeUI()

        return binding.root
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
                binding.cardViewAccountFragAccountPreviewNotSignedIn2.visibility = View.GONE
                binding.cardViewAccountFragAccountPreview2.visibility = View.VISIBLE
            } else {
                // User isn't signed in.
                binding.cardViewAccountFragAccountPreviewNotSignedIn2.visibility = View.VISIBLE
                binding.cardViewAccountFragAccountPreview2.visibility = View.GONE
                binding.cardViewAccountFragAccountDetail.visibility = View.GONE
                visible = false
            }
        }

        binding.cardViewAccountFragAccountPreview2.setOnClickListener {
            binding.cardViewAccountFragAccountDetail.visibility = if (visible) {
                visible = false
                View.GONE
            } else {
                visible = true
                View.VISIBLE
            }
        }
    }
}