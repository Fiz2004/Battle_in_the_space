package com.fiz.android.battleinthespace.base.presentation.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.base.data.PlayerRepository
import com.fiz.android.battleinthespace.base.presentation.MainActivity
import com.fiz.android.battleinthespace.base.presentation.MainViewModel
import com.fiz.android.battleinthespace.base.presentation.MainViewModelFactory
import com.fiz.android.battleinthespace.base.presentation.dialoghelper.DialogHelper
import com.fiz.android.battleinthespace.databinding.FragmentOptionsBinding
import com.google.firebase.auth.FirebaseUser

class OptionsFragment : Fragment() {
    private val dialogHelper by lazy { DialogHelper(requireActivity() as MainActivity) }
    private var _binding: FragmentOptionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by lazy {
        val viewModelFactory = MainViewModelFactory(PlayerRepository.get())
        ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOptionsBinding.inflate(inflater, container, false)

        binding.mainViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.onePlayer.signUpOut.setOnClickListener {
            if (binding.onePlayer.signUpOut.text == "Sign_up") {
                dialogHelper.createSignDialog(DialogHelper.SIGN_UP_STATE)
            } else {
                viewModel.user.value = null
                viewModel.mAuth.signOut()
                dialogHelper.accHelper.signInOutG()
            }
        }

        binding.onePlayer.signIn.setOnClickListener {
            dialogHelper.createSignDialog(DialogHelper.SIGN_IN_STATE)
        }

        binding.twoPlayer.signUpOut.visibility = View.GONE
        binding.twoPlayer.signIn.visibility = View.GONE
        binding.twoPlayer.email.visibility = View.GONE
        binding.threePlayer.signUpOut.visibility = View.GONE
        binding.threePlayer.signIn.visibility = View.GONE
        binding.threePlayer.email.visibility = View.GONE
        binding.fourPlayer.signUpOut.visibility = View.GONE
        binding.fourPlayer.signIn.visibility = View.GONE
        binding.fourPlayer.email.visibility = View.GONE

        viewModel.user.observe(requireActivity()) {
            if (binding.onePlayer.signUpOut.text == "Sign_up") {
                uiUpdate(it)
            } else {
                uiUpdate(null)
            }
        }


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(viewModel.mAuth.currentUser)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun uiUpdate(user: FirebaseUser?) {
        if (user == null) {
            binding.onePlayer.email.text = "No email"
            binding.onePlayer.signUpOut.text = "Sign_up"
            binding.onePlayer.signIn.text = "Sign_in"
            binding.onePlayer.signIn.visibility = View.VISIBLE
        } else {
            binding.onePlayer.email.text = user.email
            binding.onePlayer.signUpOut.text = "Sign_out"
            binding.onePlayer.signIn.visibility = View.GONE
        }
    }
}