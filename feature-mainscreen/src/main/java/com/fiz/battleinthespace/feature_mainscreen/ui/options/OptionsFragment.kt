package com.fiz.battleinthespace.feature_mainscreen.ui.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fiz.battleinthespace.common.setVisible
import com.fiz.battleinthespace.feature_mainscreen.R
import com.fiz.battleinthespace.feature_mainscreen.databinding.FragmentOptionsBinding
import com.fiz.battleinthespace.feature_mainscreen.ui.AccountViewModel
import com.fiz.battleinthespace.feature_mainscreen.ui.MainViewModel
import com.fiz.battleinthespace.feature_mainscreen.ui.dialoghelper.DialogHelper
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseUser

class OptionsFragment : Fragment() {
    private val dialogHelper by lazy { DialogHelper() }

    private val viewModel: MainViewModel by activityViewModels()

    private val accountViewModel: AccountViewModel by activityViewModels()

    private lateinit var binding: FragmentOptionsBinding
    private lateinit var playersRadioButton: List<RadioButton>
    private lateinit var playersEditText: List<TextInputEditText>
    private lateinit var playersSwitchCompat: List<SwitchMaterial>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOptionsBinding.inflate(inflater, container, false)
        playersRadioButton = listOf(
            binding.onePlayersRadioButton,
            binding.twoPlayersRadioButton,
            binding.threePlayersRadioButton,
            binding.fourPlayersRadioButton
        )

        playersEditText = listOf(
            binding.onePlayer.PlayersEditText,
            binding.twoPlayer.PlayersEditText,
            binding.threePlayer.PlayersEditText,
            binding.fourPlayer.PlayersEditText
        )

        playersSwitchCompat = listOf(
            binding.onePlayer.controllerPlayerSwitchCompat,
            binding.twoPlayer.controllerPlayerSwitchCompat,
            binding.threePlayer.controllerPlayerSwitchCompat,
            binding.fourPlayer.controllerPlayerSwitchCompat
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.countPlayer.observe(viewLifecycleOwner) {
            playersRadioButton.forEachIndexed { index, radioButton ->
                radioButton.isChecked = it == index + 1
            }

            binding.twoPlayer.root.setVisible(it >= 2)
            binding.threePlayer.root.setVisible(it >= 3)
            binding.fourPlayer.root.setVisible(it >= 4)
        }

        viewModel.players.observe(viewLifecycleOwner) {
            playersEditText.forEachIndexed { index, textInputEditText ->
                if (textInputEditText.text.toString() != it[index].name)
                    textInputEditText.setText(it[index].name)
            }

            playersSwitchCompat.forEachIndexed { index, switchMaterial ->
                switchMaterial.isChecked =
                    it[index].controllerPlayer == true

                switchMaterial.text = getString(
                    if (it[index].controllerPlayer)
                        R.string.controller_options_togglebutton_on
                    else
                        R.string.controller_options_togglebutton_off
                )
            }
        }

        binding.onePlayer.signIn.setOnClickListener {
            val args = Bundle()
            args.putInt("index", DialogHelper.SIGN_IN_STATE)
            dialogHelper.arguments = args
            dialogHelper.show(childFragmentManager, "1")
        }

        binding.onePlayer.signUp.setOnClickListener {
            val args = Bundle()
            args.putInt("index", DialogHelper.SIGN_UP_STATE)
            dialogHelper.arguments = args
            dialogHelper.show(childFragmentManager, "0")
        }

        binding.onePlayer.signOut.setOnClickListener {
            accountViewModel.signInOutG(requireActivity())
        }

        playersRadioButton.forEachIndexed { index, radioButton ->
            radioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked)
                    viewModel.setCountPlayers(index + 1)
            }
        }

        playersEditText.forEachIndexed { index, textInputEditText ->
            textInputEditText.doAfterTextChanged {
                viewModel.nameChanged(index, it.toString())
            }
        }

        playersSwitchCompat.forEachIndexed { index, switchMaterial ->
            switchMaterial.setOnCheckedChangeListener { compoundButton, isChecked ->
                viewModel.changeControllerPlayer(index, isChecked)
            }
        }

        binding.onePlayer.reset.setOnClickListener {
            viewModel.onClickReset()
        }

        binding.twoPlayer.reset.setOnClickListener {
            viewModel.onClickReset()
        }
        binding.threePlayer.reset.setOnClickListener {
            viewModel.onClickReset()
        }
        binding.fourPlayer.reset.setOnClickListener {
            viewModel.onClickReset()
        }


        accountViewModel.user.observe(requireActivity()) {
            uiUpdate(it)
        }

        binding.onePlayersRadioButton.text =
            resources.getQuantityString(R.plurals.count_players, 1, 1)
        binding.twoPlayersRadioButton.text =
            resources.getQuantityString(R.plurals.count_players, 2, 2)
        binding.threePlayersRadioButton.text =
            resources.getQuantityString(R.plurals.count_players, 3, 3)
        binding.fourPlayersRadioButton.text =
            resources.getQuantityString(R.plurals.count_players, 4, 4)

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

        binding.onePlayer.PlayersTextView.text =
            resources.getQuantityString(R.plurals.players_count, 1, 1)
        binding.twoPlayer.PlayersTextView.text =
            resources.getQuantityString(R.plurals.players_count, 2, 2)
        binding.threePlayer.PlayersTextView.text =
            resources.getQuantityString(R.plurals.players_count, 3, 3)
        binding.fourPlayer.PlayersTextView.text =
            resources.getQuantityString(R.plurals.players_count, 4, 4)
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(accountViewModel.mAuth.currentUser)
    }

    private fun uiUpdate(user: FirebaseUser?) {
        binding.onePlayer.signUp.setVisible(true)
        binding.onePlayer.signIn.setVisible(true)
        binding.onePlayer.signOut.setVisible(user != null)

        binding.onePlayer.email.text = if (user == null) {
            resources.getString(R.string.no_email)
        } else {
            user.email
        }
    }
}