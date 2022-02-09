package com.fiz.android.battleinthespace.base.presentation.dialoghelper

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.domain.AccountHelper
import com.fiz.android.battleinthespace.base.presentation.MainActivity
import com.fiz.android.battleinthespace.databinding.SignDialogBinding

class DialogHelper(private val act: MainActivity) {
    val accHelper = AccountHelper(act)
    fun createSignDialog(index: Int) {
        val builder = AlertDialog.Builder(act)
        val binding = SignDialogBinding.inflate(act.layoutInflater)
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
            accHelper.signInWithGoogle()
        }

        dialog.show()
    }

    private fun setDialogState(
        index: Int,
        binding: SignDialogBinding) {
        if (index == SIGN_UP_STATE) {
            val txt = act.resources.getString(R.string.signUp)
            binding.signUpInTitleTextView.text = txt
            binding.signUpInButton.text = txt
        } else {
            val txt = act.resources.getString(R.string.signIn)
            binding.signUpInTitleTextView.text = txt
            binding.signUpInButton.text = txt
            binding.forgotPasswordButton.visibility = View.VISIBLE
        }
    }

    private fun setOnClickSignUpIn(
        dialog: AlertDialog,
        binding: SignDialogBinding,
        index: Int) {
        dialog.dismiss()
        val email: String = binding.signEmailEditText.text.toString()
        val password: String = binding.signPasswordEditText.text.toString()
        if (index == SIGN_UP_STATE) {
            accHelper.signUpWithEmail(email, password)
        } else {
            accHelper.signInWithEmail(email, password)
        }
    }

    private fun setOnClickResetPassword(dialog: AlertDialog?, binding: SignDialogBinding) {
        if (binding.signEmailEditText.text.isNotEmpty()) {
            act.viewModel.mAuth.sendPasswordResetEmail(binding.signEmailEditText.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(act, "email was sent", Toast.LENGTH_LONG).show()
                    }
                }
            dialog?.dismiss()
        } else {
            binding.dialogMessageTextView.visibility = View.VISIBLE

        }
    }

    companion object {
        const val SIGN_UP_STATE = 0
        const val SIGN_IN_STATE = 1
    }
}