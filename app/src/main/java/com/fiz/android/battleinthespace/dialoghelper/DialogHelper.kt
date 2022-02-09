package com.fiz.android.battleinthespace.dialoghelper

import android.app.AlertDialog
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.accounthelper.AccountHelper
import com.fiz.android.battleinthespace.base.presentation.MainActivity
import com.fiz.android.battleinthespace.databinding.SignDialogBinding

class DialogHelper(private val act: MainActivity) {
    private val accHelper = AccountHelper(act)
    private lateinit var rootDialogElement: SignDialogBinding
    fun createSignDialog(index: Int) {
        val builder = AlertDialog.Builder(act)
        rootDialogElement = SignDialogBinding.inflate(act.layoutInflater)
        val view = rootDialogElement.root
        builder.setView(view)

        if (index == SIGN_UP_STATE) {
            updateUI(act.resources.getString(R.string.signUp))
        } else {
            updateUI(act.resources.getString(R.string.signIn))
        }
        val dialog = builder.create()
        rootDialogElement.signUpInButton.setOnClickListener {
            dialog.dismiss()
            val email: String = rootDialogElement.signEmailEditText.text.toString()
            val password: String = rootDialogElement.signPasswordEditText.text.toString()
            if (index == SIGN_UP_STATE) {
                accHelper.signUpWithEmail(email, password)
            } else {
                accHelper.signInWithEmail(email, password)
            }
        }
        dialog.show()
    }

    private fun updateUI(txt: String) {
        rootDialogElement.signUpInTitleTextView.text = txt
        rootDialogElement.signUpInButton.text = txt
    }

    companion object {
        const val SIGN_UP_STATE = 0
        const val SIGN_IN_STATE = 1
    }
}