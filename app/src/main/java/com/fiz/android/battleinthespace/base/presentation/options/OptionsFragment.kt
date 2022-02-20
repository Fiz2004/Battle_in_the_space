package com.fiz.android.battleinthespace.base.presentation.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.presentation.*
import com.fiz.android.battleinthespace.base.presentation.dialoghelper.DialogHelper
import com.fiz.android.battleinthespace.databinding.FragmentOptionsBinding
import com.google.firebase.auth.FirebaseUser

class OptionsFragment : Fragment() {
    private val dialogHelper by lazy { DialogHelper() }

    private val viewModel: MainViewModel by lazy {
        val viewModelFactory = MainViewModelFactory(requireActivity().applicationContext)
        ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]
    }

    private val accountViewModel: AccountViewModel by lazy {
        val viewModelFactory = AccountViewModelFactory()
        ViewModelProvider(requireActivity(), viewModelFactory)[AccountViewModel::class.java]
    }

    private lateinit var binding: FragmentOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOptionsBinding.inflate(inflater, container, false)

        binding.mainViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.onePlayer.signUp.setOnClickListener {
            val args = Bundle()
            args.putInt("index", DialogHelper.SIGN_UP_STATE)
            dialogHelper.arguments = args
            dialogHelper.show(childFragmentManager, "0")
        }

        binding.onePlayer.signOut.setOnClickListener {
            accountViewModel.signInOutG(requireActivity() as MainActivity)
        }

        binding.onePlayer.signIn.setOnClickListener {
            val args = Bundle()
            args.putInt("index", DialogHelper.SIGN_IN_STATE)
            dialogHelper.arguments = args
            dialogHelper.show(childFragmentManager, "1")
        }

        binding.twoPlayer.signUp.visibility = View.GONE
        binding.twoPlayer.signIn.visibility = View.GONE
        binding.twoPlayer.signOut.visibility = View.GONE
        binding.twoPlayer.email.visibility = View.GONE
        binding.threePlayer.signUp.visibility = View.GONE
        binding.threePlayer.signIn.visibility = View.GONE
        binding.threePlayer.signOut.visibility = View.GONE
        binding.threePlayer.email.visibility = View.GONE
        binding.fourPlayer.signUp.visibility = View.GONE
        binding.fourPlayer.signIn.visibility = View.GONE
        binding.fourPlayer.signOut.visibility = View.GONE
        binding.fourPlayer.email.visibility = View.GONE
        binding.onePlayer.PlayersEditText.setText(viewModel.playerListLiveData.value?.get(0)?.name)
        binding.twoPlayer.PlayersEditText.setText(viewModel.playerListLiveData.value?.get(1)?.name)
        binding.threePlayer.PlayersEditText.setText(viewModel.playerListLiveData.value?.get(2)?.name)
        binding.fourPlayer.PlayersEditText.setText(viewModel.playerListLiveData.value?.get(3)?.name)

        binding.onePlayer.PlayersEditText.addTextChangedListener {
            viewModel.player.name = binding.onePlayer.PlayersEditText.text.toString()
            viewModel.playerListLiveData.value?.get(0)?.name =
                binding.onePlayer.PlayersEditText.text.toString()
        }
        binding.twoPlayer.PlayersEditText.addTextChangedListener {
            viewModel.playerListLiveData.value?.get(1)?.name =
                binding.onePlayer.PlayersEditText.text.toString()
        }
        binding.threePlayer.PlayersEditText.addTextChangedListener {
            viewModel.playerListLiveData.value?.get(2)?.name =
                binding.onePlayer.PlayersEditText.text.toString()
        }
        binding.fourPlayer.PlayersEditText.addTextChangedListener {
            viewModel.playerListLiveData.value?.get(3)?.name =
                binding.onePlayer.PlayersEditText.text.toString()
        }

        accountViewModel.user.observe(requireActivity()) {
            uiUpdate(it)
        }


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(accountViewModel.mAuth.currentUser)
    }

    private fun uiUpdate(user: FirebaseUser?) {
        if (user == null) {
            binding.onePlayer.email.text = resources.getString(R.string.no_email)
            binding.onePlayer.signUp.visibility = View.VISIBLE
            binding.onePlayer.signIn.visibility = View.VISIBLE
            binding.onePlayer.signOut.visibility = View.GONE
        } else {
            binding.onePlayer.email.text = user.email
            binding.onePlayer.signUp.visibility = View.VISIBLE
            binding.onePlayer.signIn.visibility = View.VISIBLE
            binding.onePlayer.signOut.visibility = View.VISIBLE
        }
    }
}