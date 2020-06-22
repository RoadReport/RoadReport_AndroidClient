package com.txwstudio.app.roadreport.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.databinding.FragmentAccountBinding
import com.txwstudio.app.roadreport.handler.AccountFragClickHandler

class AccountFragment : Fragment() {

    private lateinit var accountViewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        accountViewModel = ViewModelProvider(this).get(AccountViewModel::class.java)

        val binding = DataBindingUtil.inflate<FragmentAccountBinding>(
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
}