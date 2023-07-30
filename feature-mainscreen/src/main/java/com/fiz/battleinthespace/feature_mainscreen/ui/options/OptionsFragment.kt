package com.fiz.battleinthespace.feature_mainscreen.ui.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fiz.battleinthespace.common.launchAndRepeatWithViewLifecycle
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
    private lateinit var playersSwitchOtherCompat: List<SwitchMaterial>

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

        playersSwitchOtherCompat = listOf(
            binding.twoPlayer.controllerPlayerSwitchCompat,
            binding.threePlayer.controllerPlayerSwitchCompat,
            binding.fourPlayer.controllerPlayerSwitchCompat
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setupListener()
        setupObserve()
    }

    private fun init() {

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

        playersSwitchOtherCompat.forEachIndexed { index, switchMaterial ->
            switchMaterial.setOnCheckedChangeListener { compoundButton, isChecked ->
                viewModel.changeControllerPlayer(index + 1, isChecked)
            }
        }

        playersRadioButton.forEachIndexed { index, radioButton ->
            radioButton.text =
                resources.getQuantityString(R.plurals.count_players, index + 1, index + 1)
        }
    }

    private fun setupListener() {
        binding.onePlayer.signIn.setOnClickListener {
            val args = Bundle()
            args.putParcelable(DialogHelper.KEY, DialogHelper.Companion.DialogState.SignIn)
            dialogHelper.arguments = args
            dialogHelper.show(childFragmentManager, DialogHelper.Companion.DialogState.SignIn.toString())
        }

        binding.onePlayer.signUp.setOnClickListener {
            val args = Bundle()
            args.putParcelable(DialogHelper.KEY, DialogHelper.Companion.DialogState.SignUp)
            dialogHelper.arguments = args
            dialogHelper.show(childFragmentManager, DialogHelper.Companion.DialogState.SignUp.toString())
        }

        binding.onePlayer.signOut.setOnClickListener {
            accountViewModel.signInOutG()
        }

        binding.onePlayer.reset.setOnClickListener {
            viewModel.onClickReset(0)
        }

        binding.twoPlayer.reset.setOnClickListener {
            viewModel.onClickReset(1)
        }
        binding.threePlayer.reset.setOnClickListener {
            viewModel.onClickReset(2)
        }
        binding.fourPlayer.reset.setOnClickListener {
            viewModel.onClickReset(3)
        }
    }

    private fun setupObserve() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewState.collect { viewState ->
                if (viewState.players.isEmpty()) return@collect

                playersRadioButton.forEachIndexed { index, radioButton ->
                    radioButton.isChecked = viewState.countPlayer == index + 1
                }

                binding.twoPlayer.root.isVisible = viewState.countPlayer >= 2
                binding.threePlayer.root.isVisible = viewState.countPlayer >= 3
                binding.fourPlayer.root.isVisible = viewState.countPlayer >= 4

                playersEditText.forEachIndexed { index, textInputEditText ->
                    if (textInputEditText.text.toString() != viewState.players[index].name)
                        textInputEditText.setText(viewState.players[index].name)
                }

                playersSwitchOtherCompat.forEachIndexed { index, switchMaterial ->
                    switchMaterial.isChecked =
                        viewState.players[index + 1].controllerPlayer == true

                    switchMaterial.text = getString(
                        if (viewState.players[index + 1].controllerPlayer)
                            R.string.controller_options_togglebutton_on
                        else
                            R.string.controller_options_togglebutton_off
                    )
                }

            }
        }

        launchAndRepeatWithViewLifecycle {
            accountViewModel.email.collect { email ->
                binding.onePlayer.signUp.isVisible = email == null
                binding.onePlayer.signIn.isVisible = email == null
                binding.onePlayer.signOut.isVisible = email != null

                binding.onePlayer.email.text = email ?: resources.getString(R.string.no_email)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}