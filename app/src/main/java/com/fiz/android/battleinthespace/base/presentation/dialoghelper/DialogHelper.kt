package com.fiz.android.battleinthespace.base.presentation.dialoghelper

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.data.PlayerRepository
import com.fiz.android.battleinthespace.base.presentation.MainActivity
import com.fiz.android.battleinthespace.base.presentation.MainViewModel
import com.fiz.android.battleinthespace.base.presentation.MainViewModelFactory
import com.fiz.android.battleinthespace.databinding.SignDialogBinding


class DialogHelper : DialogFragment() {
    private val viewModel: MainViewModel by lazy {
        val viewModelFactory = MainViewModelFactory(PlayerRepository.get())
        ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val index = requireArguments().getInt("index")

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
            viewModel.signUpWithEmail(email, password)
        } else {
            viewModel.signInWithEmail(email, password)
        }
    }

    private fun setOnClickResetPassword(dialog: AlertDialog?, binding: SignDialogBinding) {
        if (binding.signEmailEditText.text.isNotEmpty()) {
            (requireActivity() as MainActivity).viewModel.mAuth.sendPasswordResetEmail(binding.signEmailEditText.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireActivity(), "email was sent", Toast.LENGTH_LONG).show()
                    }
                }
            dialog?.dismiss()
        } else {
            binding.dialogMessageTextView.visibility = View.VISIBLE

        }
    }

    private fun setOnClickSignInGoogle(dialog: AlertDialog?) {
        viewModel.signInWithGoogle(requireActivity() as MainActivity)
        dialog?.dismiss()
    }

    companion object {
        const val SIGN_UP_STATE = 0
        const val SIGN_IN_STATE = 1
    }
}