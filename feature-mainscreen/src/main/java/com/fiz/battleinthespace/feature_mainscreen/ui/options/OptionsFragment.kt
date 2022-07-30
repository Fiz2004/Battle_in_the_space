package com.fiz.battleinthespace.feature_mainscreen.ui.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fiz.battleinthespace.common.launchAndRepeatWithViewLifecycle
import com.fiz.battleinthespace.common.setVisible
import com.fiz.battleinthespace.feature_mainscreen.R
import com.fiz.battleinthespace.feature_mainscreen.databinding.FragmentOptionsBinding
import com.fiz.battleinthespace.feature_mainscreen.ui.AccountViewModel
import com.fiz.battleinthespace.feature_mainscreen.ui.MainViewModel
import com.fiz.battleinthespace.feature_mainscreen.ui.dialoghelper.DialogHelper
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class OptionsFragment : Fragment() {
    private val dialogHelper by lazy { DialogHelper() }

    private val viewModel: MainViewModel by activityViewModels()

    private val accountViewModel: AccountViewModel by activityViewModels()

    private var _binding: FragmentOptionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var playersRadioButton: List<RadioButton>
    private lateinit var playersLabelEditText: List<TextInputLayout>
    private lateinit var playersEditText: List<TextInputEditText>
    private lateinit var playersSwitchCompat: List<SwitchMaterial>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOptionsBinding.inflate(inflater, container, false)
        playersRadioButton = listOf(
            binding.onePlayersRadioButton,
            binding.twoPlayersRadioButton,
            binding.threePlayersRadioButton,
            binding.fourPlayersRadioButton
        )

        playersLabelEditText = listOf(
            binding.onePlayer.labelPlayersEditText,
            binding.twoPlayer.labelPlayersEditText,
            binding.threePlayer.labelPlayersEditText,
            binding.fourPlayer.labelPlayersEditText
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

        playersLabelEditText.forEachIndexed { index, textInputLayout ->
            textInputLayout.hint = getString(
                R.string.name_options_edittext,
                resources.getQuantityString(R.plurals.players_count, index + 1, index + 1)
            )
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

        playersRadioButton.forEachIndexed { index, radioButton ->
            radioButton.text =
                resources.getQuantityString(R.plurals.count_players, index + 1, index + 1)
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

        binding.onePlayer.signIn.setOnClickListener {
            val args = Bundle()
            args.putInt(DialogHelper.KEY, DialogHelper.SIGN_IN_STATE)
            dialogHelper.arguments = args
            dialogHelper.show(childFragmentManager, DialogHelper.SIGN_IN_STATE.toString())
        }

        binding.onePlayer.signUp.setOnClickListener {
            val args = Bundle()
            args.putInt(DialogHelper.KEY, DialogHelper.SIGN_UP_STATE)
            dialogHelper.arguments = args
            dialogHelper.show(childFragmentManager, DialogHelper.SIGN_UP_STATE.toString())
        }

        binding.onePlayer.signOut.setOnClickListener {
            accountViewModel.signInOutG()
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

        launchAndRepeatWithViewLifecycle {
            viewModel.viewState.collect { viewState ->
                if (viewState.players.isEmpty()) return@collect

                playersRadioButton.forEachIndexed { index, radioButton ->
                    radioButton.isChecked = viewState.countPlayer == index + 1
                }

                binding.twoPlayer.root.setVisible(viewState.countPlayer >= 2)
                binding.threePlayer.root.setVisible(viewState.countPlayer >= 3)
                binding.fourPlayer.root.setVisible(viewState.countPlayer >= 4)

                playersEditText.forEachIndexed { index, textInputEditText ->
                    if (textInputEditText.text.toString() != viewState.players[index].name)
                        textInputEditText.setText(viewState.players[index].name)
                }

                playersSwitchCompat.forEachIndexed { index, switchMaterial ->
                    switchMaterial.isChecked =
                        viewState.players[index].controllerPlayer == true

                    switchMaterial.text = getString(
                        if (viewState.players[index].controllerPlayer)
                            R.string.controller_options_togglebutton_on
                        else
                            R.string.controller_options_togglebutton_off
                    )
                }

            }
        }

        launchAndRepeatWithViewLifecycle {
            accountViewModel.email.collect { email ->
                binding.onePlayer.signUp.setVisible(email == null)
                binding.onePlayer.signIn.setVisible(email == null)
                binding.onePlayer.signOut.setVisible(email != null)

                binding.onePlayer.email.text = email ?: resources.getString(R.string.no_email)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}