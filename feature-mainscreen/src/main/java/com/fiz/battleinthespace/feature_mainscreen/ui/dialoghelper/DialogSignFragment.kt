package com.fiz.battleinthespace.feature_mainscreen.ui.dialoghelper

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.fiz.battleinthespace.common.parcelable
import com.fiz.battleinthespace.feature_mainscreen.R
import com.fiz.battleinthespace.feature_mainscreen.databinding.SignDialogBinding
import com.fiz.battleinthespace.feature_mainscreen.ui.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
internal class DialogSignFragment : DialogFragment() {

    private val viewModel: DialogSignViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogState =
            requireArguments().parcelable(DialogSignFragment::class.simpleName, DialogState::class.java)
                ?: throw IllegalArgumentException("Argument not DialogState")

        val builder = AlertDialog.Builder(requireActivity())
        val binding = SignDialogBinding.inflate(requireActivity().layoutInflater)
        val view = binding.root
        builder.setView(view)

        setDialogState(dialogState, binding)
        val dialog = builder.create()

        binding.signUpInButton.setOnClickListener {
            setOnClickSignUpIn(dialog, binding, dialogState)
        }
        binding.forgotPasswordButton.setOnClickListener {
            setOnClickResetPassword(dialog, binding)
        }
        binding.signInGoogle.setOnClickListener {
            setOnClickSignInGoogle(dialog)
        }

        return dialog
    }

    private fun setDialogState(
        dialogState: DialogState,
        binding: SignDialogBinding
    ) {
        val stringId = when (dialogState) {
            DialogState.SignUp -> R.string.signUp
            DialogState.SignIn -> R.string.signIn
        }
        val txt = resources.getString(stringId)
        binding.signUpInTitleTextView.text = txt
        binding.signUpInButton.text = txt
        binding.forgotPasswordButton.isVisible = dialogState != DialogState.SignUp
    }

    private fun setOnClickSignUpIn(
        dialog: AlertDialog,
        binding: SignDialogBinding,
        dialogState: DialogState
    ) {
        dialog.dismiss()
        val email: String = binding.signEmailEditText.text.toString()
        val password: String = binding.signPasswordEditText.text.toString()
        when (dialogState) {
            DialogState.SignUp -> viewModel.signUpWithEmail(email, password)
            DialogState.SignIn -> viewModel.signInWithEmail(email, password)
        }
    }

    private fun setOnClickResetPassword(dialog: AlertDialog?, binding: SignDialogBinding) {
        if (binding.signEmailEditText.text.isNotEmpty()) {
            viewModel.resetPassword(binding.signEmailEditText.text.toString())
            dialog?.dismiss()
        } else {
            binding.dialogMessageTextView.visibility = View.VISIBLE
        }
    }


    private fun setOnClickSignInGoogle(dialog: AlertDialog?) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val signInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val intent = signInClient.signInIntent

        (requireActivity() as MainActivity).signInGoogle(intent)
        dialog?.dismiss()
    }

    companion object {

        @Parcelize
        enum class DialogState : Parcelable {
            SignUp, SignIn
        }
    }
}