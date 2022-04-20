package com.fiz.battleinthespace.feature_mainscreen.ui.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fiz.battleinthespace.App
import com.fiz.battleinthespace.feature_mainscreen.R
import com.fiz.battleinthespace.feature_mainscreen.databinding.FragmentOptionsBinding
import com.fiz.battleinthespace.feature_mainscreen.ui.AccountViewModel
import com.fiz.battleinthespace.feature_mainscreen.ui.MainViewModel
import com.fiz.battleinthespace.feature_mainscreen.ui.MainViewModelFactory
import com.fiz.battleinthespace.feature_mainscreen.ui.dialoghelper.DialogHelper
import com.google.firebase.auth.FirebaseUser

class OptionsFragment : Fragment() {
    private val dialogHelper by lazy { DialogHelper() }

    private val viewModel: MainViewModel by activityViewModels {
        val app = requireActivity().application as com.fiz.battleinthespace.App
        MainViewModelFactory(app.playerRepository)
    }

    private val accountViewModel: AccountViewModel by activityViewModels()

    private lateinit var binding: FragmentOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOptionsBinding.inflate(inflater, container, false)

        binding.onePlayer.signUp.setOnClickListener {
            val args = Bundle()
            args.putInt("index", DialogHelper.SIGN_UP_STATE)
            dialogHelper.arguments = args
            dialogHelper.show(childFragmentManager, "0")
        }

        binding.onePlayer.signOut.setOnClickListener {
            accountViewModel.signInOutG(requireActivity())
        }

        binding.onePlayer.signIn.setOnClickListener {
            val args = Bundle()
            args.putInt("index", DialogHelper.SIGN_IN_STATE)
            dialogHelper.arguments = args
            dialogHelper.show(childFragmentManager, "1")
        }


        binding.onePlayersRadioButton.setOnCheckedChangeListener { _, b ->
            if (b)
                viewModel.setCountPlayers(1)
            updateUI()
        }
        binding.twoPlayersRadioButton.setOnCheckedChangeListener { _, b ->
            if (b)
                viewModel.setCountPlayers(2)
            updateUI()
        }
        binding.threePlayersRadioButton.setOnCheckedChangeListener { _, b ->
            if (b)
                viewModel.setCountPlayers(3)
            updateUI()
        }
        binding.fourPlayersRadioButton.setOnCheckedChangeListener { _, b ->
            if (b)
                viewModel.setCountPlayers(4)
            updateUI()
        }

        binding.onePlayer.PlayersEditText.setText(viewModel.players.value?.get(0)?.name)
        binding.twoPlayer.PlayersEditText.setText(viewModel.players.value?.get(1)?.name)
        binding.threePlayer.PlayersEditText.setText(viewModel.players.value?.get(2)?.name)
        binding.fourPlayer.PlayersEditText.setText(viewModel.players.value?.get(3)?.name)

        binding.onePlayer.PlayersEditText.addTextChangedListener {
            viewModel.players.value?.get(0)?.name =
                binding.onePlayer.PlayersEditText.text.toString()
        }

        binding.twoPlayer.PlayersEditText.addTextChangedListener {
            viewModel.players.value?.get(1)?.name =
                binding.twoPlayer.PlayersEditText.text.toString()
        }

        binding.threePlayer.PlayersEditText.addTextChangedListener {
            viewModel.players.value?.get(2)?.name =
                binding.threePlayer.PlayersEditText.text.toString()
        }

        binding.fourPlayer.PlayersEditText.addTextChangedListener {
            viewModel.players.value?.get(3)?.name =
                binding.fourPlayer.PlayersEditText.text.toString()
        }


        binding.onePlayer.controllerPlayerSwitchCompat.isChecked =
            viewModel.getController(0) == true
        binding.twoPlayer.controllerPlayerSwitchCompat.isChecked =
            viewModel.getController(1) == true
        binding.threePlayer.controllerPlayerSwitchCompat.isChecked =
            viewModel.getController(2) == true
        binding.fourPlayer.controllerPlayerSwitchCompat.isChecked =
            viewModel.getController(3) == true
        binding.onePlayer.controllerPlayerSwitchCompat.setOnCheckedChangeListener { compoundButton, b ->
            viewModel.players.value?.get(0)?.controllerPlayer = b
            updateUI()
        }
        binding.twoPlayer.controllerPlayerSwitchCompat.setOnCheckedChangeListener { compoundButton, b ->
            viewModel.players.value?.get(1)?.controllerPlayer = b
            updateUI()
        }
        binding.threePlayer.controllerPlayerSwitchCompat.setOnCheckedChangeListener { compoundButton, b ->
            viewModel.players.value?.get(2)?.controllerPlayer = b
            updateUI()
        }
        binding.fourPlayer.controllerPlayerSwitchCompat.setOnCheckedChangeListener { compoundButton, b ->
            viewModel.players.value?.get(3)?.controllerPlayer = b
            updateUI()
        }


        binding.onePlayer.reset.setOnClickListener {
            viewModel.onClickReset(1)
        }

        binding.twoPlayer.reset.setOnClickListener {
            viewModel.onClickReset(2)
        }
        binding.threePlayer.reset.setOnClickListener {
            viewModel.onClickReset(3)
        }
        binding.fourPlayer.reset.setOnClickListener {
            viewModel.onClickReset(4)
        }


        accountViewModel.user.observe(requireActivity()) {
            uiUpdate(it)
        }

        updateUI()
        return binding.root
    }

    private fun updateUI() {
        binding.onePlayersRadioButton.text =
            resources.getQuantityString(R.plurals.count_players, 1, 1)
        binding.twoPlayersRadioButton.text =
            resources.getQuantityString(R.plurals.count_players, 2, 2)
        binding.threePlayersRadioButton.text =
            resources.getQuantityString(R.plurals.count_players, 3, 3)
        binding.fourPlayersRadioButton.text =
            resources.getQuantityString(R.plurals.count_players, 4, 4)

        if (viewModel.countPlayerLiveDataCompare(2))
            binding.twoPlayer.root.visibility = View.VISIBLE
        else
            binding.twoPlayer.root.visibility = View.GONE
        if (viewModel.countPlayerLiveDataCompare(3))
            binding.threePlayer.root.visibility = View.VISIBLE
        else
            binding.threePlayer.root.visibility = View.GONE
        if (viewModel.countPlayerLiveDataCompare(4))
            binding.fourPlayer.root.visibility = View.VISIBLE
        else
            binding.fourPlayer.root.visibility = View.GONE

        if (viewModel.countPlayerLiveDataEquals(1)) {
            binding.onePlayersRadioButton.isChecked = true
            binding.twoPlayersRadioButton.isChecked = false
            binding.threePlayersRadioButton.isChecked = false
            binding.fourPlayersRadioButton.isChecked = false
        }
        if (viewModel.countPlayerLiveDataEquals(2)) {
            binding.onePlayersRadioButton.isChecked = false
            binding.twoPlayersRadioButton.isChecked = true
            binding.threePlayersRadioButton.isChecked = false
            binding.fourPlayersRadioButton.isChecked = false
        }
        if (viewModel.countPlayerLiveDataEquals(3)) {
            binding.onePlayersRadioButton.isChecked = false
            binding.twoPlayersRadioButton.isChecked = false
            binding.threePlayersRadioButton.isChecked = true
            binding.fourPlayersRadioButton.isChecked = false
        }
        if (viewModel.countPlayerLiveDataEquals(4)) {
            binding.onePlayersRadioButton.isChecked = false
            binding.twoPlayersRadioButton.isChecked = false
            binding.threePlayersRadioButton.isChecked = false
            binding.fourPlayersRadioButton.isChecked = true
        }

        binding.onePlayer.controllerPlayerSwitchCompat.text =
            if (viewModel.getController(0))
                resources.getString(R.string.controller_options_togglebutton_on)
            else
                resources.getString(R.string.controller_options_togglebutton_off)
        binding.twoPlayer.controllerPlayerSwitchCompat.text =
            if (viewModel.getController(1))
                resources.getString(R.string.controller_options_togglebutton_on)
            else
                resources.getString(R.string.controller_options_togglebutton_off)
        binding.threePlayer.controllerPlayerSwitchCompat.text =
            if (viewModel.getController(2))
                resources.getString(R.string.controller_options_togglebutton_on)
            else
                resources.getString(R.string.controller_options_togglebutton_off)
        binding.fourPlayer.controllerPlayerSwitchCompat.text =
            if (viewModel.getController(3))
                resources.getString(R.string.controller_options_togglebutton_on)
            else
                resources.getString(R.string.controller_options_togglebutton_off)

        binding.onePlayer.PlayersTextView.text =
            resources.getQuantityString(R.plurals.players_count, 1, 1)
        binding.twoPlayer.PlayersTextView.text =
            resources.getQuantityString(R.plurals.players_count, 2, 2)
        binding.threePlayer.PlayersTextView.text =
            resources.getQuantityString(R.plurals.players_count, 3, 3)
        binding.fourPlayer.PlayersTextView.text =
            resources.getQuantityString(R.plurals.players_count, 4, 4)


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