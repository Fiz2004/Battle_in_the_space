package com.fiz.battleinthespace.feature_mainscreen.ui.dialoghelper

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.fiz.battleinthespace.feature_mainscreen.R
import com.fiz.battleinthespace.feature_mainscreen.databinding.SignDialogBinding
import com.fiz.battleinthespace.feature_mainscreen.ui.AccountViewModel
import com.fiz.battleinthespace.feature_mainscreen.ui.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class DialogHelper : DialogFragment() {
    private val accountViewModel: AccountViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val index = requireArguments().getInt(KEY)

        val builder = AlertDialog.Builder(requireActivity())
        val binding = SignDialogBinding.inflate(requireActivity().layoutInflater)
        val view = binding.root
        builder.setView(view)

        setDialogState(index, binding)
        val dialog = builder.create()

        binding.signUpInButton.setOnClickListener {
            setOnClickSignUpIn(dialog, binding, index)
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
        index: Int,
        binding: SignDialogBinding
    ) {
        if (index == SIGN_UP_STATE) {
            val txt = resources.getString(R.string.signUp)
            binding.signUpInTitleTextView.text = txt
            binding.signUpInButton.text = txt
        } else {
            val txt = resources.getString(R.string.signIn)
            binding.signUpInTitleTextView.text = txt
            binding.signUpInButton.text = txt
            binding.forgotPasswordButton.visibility = View.VISIBLE
        }
    }

    private fun setOnClickSignUpIn(
        dialog: AlertDialog,
        binding: SignDialogBinding,
        index: Int
    ) {
        dialog.dismiss()
        val email: String = binding.signEmailEditText.text.toString()
        val password: String = binding.signPasswordEditText.text.toString()
        if (index == SIGN_UP_STATE) {
            accountViewModel.signUpWithEmail(email, password)
        } else {
            accountViewModel.signInWithEmail(email, password)
        }
    }

    private fun setOnClickResetPassword(dialog: AlertDialog?, binding: SignDialogBinding) {
        if (binding.signEmailEditText.text.isNotEmpty()) {
            accountViewModel.resetPassword(binding.signEmailEditText.text.toString())
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

        (requireActivity() as MainActivity).signInLauncher.launch(intent)
        dialog?.dismiss()
    }

    companion object {
        const val SIGN_UP_STATE = 0
        const val SIGN_IN_STATE = 1
        const val KEY = "index"
    }
}