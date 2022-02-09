package com.fiz.android.battleinthespace.dialoghelper

import android.app.AlertDialog
import com.fiz.android.battleinthespace.accounthelper.AccountHelper
import com.fiz.android.battleinthespace.base.presentation.MainActivity
import com.fiz.android.battleinthespace.databinding.SignDialogBinding

class DialogHelper(val act: MainActivity) {
    private val accHelper = AccountHelper(act)
    fun createSignDialog(index: Int) {
        val builder = AlertDialog.Builder(act)
        val rootDialogElement = SignDialogBinding.inflate(act.layoutInflater)
        val view = rootDialogElement.root
        builder.setView(view)

        if (index == DialogConst.SIGN_UP_STATE) {
            rootDialogElement.tvSignTitle.text = "Register"
            rootDialogElement.btSignUpIn.text = "Sign Up"
        } else {
            rootDialogElement.tvSignTitle.text = "Enter"
            rootDialogElement.btSignUpIn.text = "Sign In"
        }
        val dialog = builder.create()
        rootDialogElement.btSignUpIn.setOnClickListener {
            dialog.dismiss()
            if (index == DialogConst.SIGN_UP_STATE) {
                accHelper.signUpWithEmail(
                    rootDialogElement.edSignEmail.text.toString(),
                    rootDialogElement.edSignPassword.text.toString())
            } else {
                accHelper.signInWithEmail(
                    rootDialogElement.edSignEmail.text.toString(),
                    rootDialogElement.edSignPassword.text.toString())
            }
        }
        dialog.show()
    }
}