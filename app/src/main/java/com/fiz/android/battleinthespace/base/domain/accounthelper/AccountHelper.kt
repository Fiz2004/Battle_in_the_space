package com.fiz.android.battleinthespace.base.domain

import android.widget.Toast
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.presentation.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser

class AccountHelper(private val act: MainActivity) {
    private lateinit var signInClient: GoogleSignInClient

    fun signUpWithEmail(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) return
        act.viewModel.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                sendEmailVerification(task.result?.user!!)
                act.viewModel.email.value = task.result?.user!!
            } else {
                Toast.makeText(act, "Error sign up", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signInWithGoogle() {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        act.startActivityForResult(intent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id)).build()
        return GoogleSignIn.getClient(act, gso)
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) return
        act.viewModel.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                act.viewModel.email.value = task.result?.user!!
            } else {
                Toast.makeText(act, "Error sign in", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(act, "send email", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(act, "Error send email", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        const val GOOGLE_SIGN_IN_REQUEST_CODE = 132
    }
}