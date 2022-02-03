package com.fiz.android.battleinthespace.accounthelper

import android.widget.Toast
import com.fiz.android.battleinthespace.interfaces.MainActivity

class AccountHelper(private val act: MainActivity) {
    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() || password.isNotEmpty()) return
        act.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {

            } else {
                Toast.makeText(act, "Error sign up", Toast.LENGTH_LONG).show()
            }
        }
    }
}