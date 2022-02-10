package com.fiz.android.battleinthespace.base.domain.accounthelper

import android.util.Log
import android.widget.Toast
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.presentation.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*

class AccountHelper(private val act: MainActivity) {
    private lateinit var signInClient: GoogleSignInClient

    fun signUpWithEmail(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) return
        act.viewModel.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                sendEmailVerification(task.result?.user!!)
                act.viewModel.user.value = task.result?.user!!
            } else {
                if (task.exception is FirebaseAuthUserCollisionException) {
                    val exception = task.exception as FirebaseAuthUserCollisionException
                    if (exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                        Toast.makeText(act, FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE, Toast.LENGTH_LONG).show()
                    }
                }
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    val exception = task.exception as FirebaseAuthInvalidCredentialsException
                    if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                        Toast.makeText(act, FirebaseAuthConstants.ERROR_INVALID_EMAIL, Toast.LENGTH_LONG).show()
                    }
                }
                if (task.exception is FirebaseAuthWeakPasswordException) {
                    val exception = task.exception as FirebaseAuthWeakPasswordException
                    if (exception.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {
                        Toast.makeText(act, FirebaseAuthConstants.ERROR_WEAK_PASSWORD, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    //TODO Переделать на новый старт активити
    fun signInWithGoogle() {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        act.startActivityForResult(intent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    //TODO Сделать замену на classpath 'com.google.gms:google-services:4.3.10'
    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(act, gso)
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        act.viewModel.mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(act, "Sign in done", Toast.LENGTH_LONG).show()
                act.viewModel.user.value = task.result?.user
            } else {
                Log.d("MyLog", "Google sign in exception: ${task.exception}")
                Toast.makeText(act, "Error Sign in done", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) return
        act.viewModel.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                act.viewModel.user.value = task.result?.user!!
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    val exception = task.exception as FirebaseAuthInvalidCredentialsException
                    if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                        Toast.makeText(act, FirebaseAuthConstants.ERROR_INVALID_EMAIL, Toast.LENGTH_LONG).show()
                    } else if (exception.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                        Toast.makeText(act, FirebaseAuthConstants.ERROR_WRONG_PASSWORD, Toast.LENGTH_LONG).show()
                    }
                }
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

        object FirebaseAuthConstants {
            const val ERROR_EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE"
            const val ERROR_INVALID_EMAIL = "ERROR_INVALID_EMAIL"
            const val ERROR_WRONG_PASSWORD = "ERROR_WRONG_PASSWORD"
            const val ERROR_WEAK_PASSWORD = "ERROR_WEAK_PASSWORD"
        }
    }
}