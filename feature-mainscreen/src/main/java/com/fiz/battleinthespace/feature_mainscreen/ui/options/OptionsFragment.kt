package com.fiz.battleinthespace.feature_mainscreen.ui.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fiz.battleinthespace.common.collectUiState
import com.fiz.battleinthespace.feature_mainscreen.R
import com.fiz.battleinthespace.feature_mainscreen.databinding.FragmentOptionsBinding
import com.fiz.battleinthespace.feature_mainscreen.ui.dialoghelper.DialogSignFragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class OptionsFragment : Fragment() {

    private val dialogSignFragment by lazy { DialogSignFragment() }

    private val viewModel: OptionsViewModel by viewModels()

    private var _binding: FragmentOptionsBinding? = null
    private val binding
        get() = checkNotNull(_binding)

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
            binding.onePlayer.txtName,
            binding.twoPlayer.txtName,
            binding.threePlayer.txtName,
            binding.fourPlayer.txtName
        )

        playersEditText = listOf(
            binding.onePlayer.edtName,
            binding.twoPlayer.edtName,
            binding.threePlayer.edtName,
            binding.fourPlayer.edtName
        )

        playersSwitchOtherCompat = listOf(
            binding.twoPlayer.swController,
            binding.threePlayer.swController,
            binding.fourPlayer.swController
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.init()
        binding.setupListeners()

        collectUiState(viewModel.viewState, { binding.collectUiState(it) })
    }

    private fun FragmentOptionsBinding.init() {

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
            switchMaterial.setOnCheckedChangeListener { _, isChecked ->
                viewModel.changeControllerPlayer(index + 1, isChecked)
            }
        }

        playersRadioButton.forEachIndexed { index, radioButton ->
            radioButton.text =
                resources.getQuantityString(R.plurals.count_players, index + 1, index + 1)
        }
    }

    private fun FragmentOptionsBinding.setupListeners() {
        onePlayer.btnSignIn.setOnClickListener {
            val args = Bundle()
            args.putParcelable(DialogSignFragment::class.simpleName, DialogSignFragment.Companion.DialogState.SignIn)
            dialogSignFragment.arguments = args
            dialogSignFragment.show(childFragmentManager, DialogSignFragment.Companion.DialogState.SignIn.toString())
        }

        onePlayer.btnSignUp.setOnClickListener {
            val args = Bundle()
            args.putParcelable(DialogSignFragment::class.simpleName, DialogSignFragment.Companion.DialogState.SignUp)
            dialogSignFragment.arguments = args
            dialogSignFragment.show(childFragmentManager, DialogSignFragment.Companion.DialogState.SignUp.toString())
        }

        onePlayer.btnSignOut.setOnClickListener {
            viewModel.signInOutG()
        }

        onePlayer.btnReset.setOnClickListener {
            viewModel.onClickReset(0)
        }

        twoPlayer.btnReset.setOnClickListener {
            viewModel.onClickReset(1)
        }
        threePlayer.btnReset.setOnClickListener {
            viewModel.onClickReset(2)
        }
        fourPlayer.btnReset.setOnClickListener {
            viewModel.onClickReset(3)
        }
    }

    private fun FragmentOptionsBinding.collectUiState(viewState: OptionsViewState) {
        progress.isVisible = viewState.isLoading
        mainContent.isVisible = !viewState.isLoading

        if (viewState.players.isEmpty()) return

        playersRadioButton.forEachIndexed { index, radioButton ->
            radioButton.isChecked = viewState.countPlayer == index + 1
        }

        twoPlayer.root.isVisible = viewState.countPlayer >= 2
        threePlayer.root.isVisible = viewState.countPlayer >= 3
        fourPlayer.root.isVisible = viewState.countPlayer >= 4

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

        onePlayer.btnSignUp.isVisible = viewState.email == null
        onePlayer.btnSignIn.isVisible = viewState.email == null
        onePlayer.btnSignOut.isVisible = viewState.email != null

        onePlayer.txtEmail.text = viewState.email ?: resources.getString(R.string.no_email)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}