package com.fiz.android.battleinthespace.base.domain.accounthelper

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.data.PlayerRepository
import com.fiz.android.battleinthespace.base.presentation.MainActivity
import com.fiz.android.battleinthespace.base.presentation.MainViewModel
import com.fiz.android.battleinthespace.base.presentation.MainViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*

class AccountHelper(private val act: MainActivity) {
    val viewModel: MainViewModel by lazy {
        val viewModelFactory = MainViewModelFactory(PlayerRepository.get())
        ViewModelProvider(act, viewModelFactory)[MainViewModel::class.java]
    }

    fun signUpWithEmail(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) return
        viewModel.mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { onCompleteListenerSignUpWithEmail(it, email, password) }
    }

    private fun onCompleteListenerSignUpWithEmail(task: Task<AuthResult>, email: String, password: String) {
        if (task.isSuccessful) {
            sendEmailVerification(task.result?.user!!)
            viewModel.user.value = task.result?.user!!
        } else {
            if (printInfoExceptionAndResolveProcess(task))
                linkEmailToG(email, password)
        }
    }

    private fun linkEmailToG(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        viewModel.mAuth.currentUser?.linkWithCredential(credential)
            ?.addOnCompleteListener { onCompleteListenerLinkEmailToG(it) }
    }

    private fun onCompleteListenerLinkEmailToG(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            Toast.makeText(act, "Link ok", Toast.LENGTH_LONG).show()
        } else {
            printInfoExceptionAndResolveProcess(task)
        }
    }

    //TODO Переделать на новый старт активити
    fun signInWithGoogle() {
        val signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        act.startActivityForResult(intent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    fun signInOutG() {
        getSignInClient().signOut()
    }

    //TODO Разобраться почему R.string не найден classpath 'com.google.gms:google-services:4.3.10'
    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(act, gso)
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        if (viewModel.mAuth != null) {
            viewModel.mAuth.signInWithCredential(credential)
                .addOnCompleteListener { onCompleteListenerSignInFirebaseWithGoogle(it) }
        } else {
            Toast.makeText(act, "У вас уже есть аккаунт с таким email, войдите сначала через почту", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun onCompleteListenerSignInFirebaseWithGoogle(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            Toast.makeText(act, "Sign in done", Toast.LENGTH_LONG).show()
            viewModel.user.value = task.result?.user
        } else {
            Log.d("MyLog", "Google sign in exception: ${task.exception}")
            Toast.makeText(act, "Error Sign in done", Toast.LENGTH_LONG).show()
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) return
        viewModel.mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { onCompleteListenerSignInWithEmail(it) }
    }

    private fun onCompleteListenerSignInWithEmail(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            viewModel.user.value = task.result?.user!!
        } else {
            printInfoExceptionAndResolveProcess(task)
        }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification()
            .addOnCompleteListener { onCompleteListenerSendEmailVerification(it) }
    }

    private fun onCompleteListenerSendEmailVerification(task: Task<Void>) {
        if (task.isSuccessful) {
            Toast.makeText(act, "send email", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(act, "Error send email", Toast.LENGTH_LONG).show()
        }
    }

    private fun printInfoExceptionAndResolveProcess(task: Task<AuthResult>): Boolean {
        if (task.exception is FirebaseAuthUserCollisionException) {
            val exception = task.exception as FirebaseAuthUserCollisionException
            if (exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                Toast.makeText(act, FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE, Toast.LENGTH_LONG).show()
                return true
            }
        }
        if (task.exception is FirebaseAuthInvalidCredentialsException) {
            val exception = task.exception as FirebaseAuthInvalidCredentialsException
            if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                Toast.makeText(act, FirebaseAuthConstants.ERROR_INVALID_EMAIL, Toast.LENGTH_LONG).show()
            }
            if (exception.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                Toast.makeText(act, FirebaseAuthConstants.ERROR_WRONG_PASSWORD, Toast.LENGTH_LONG).show()
            }
        }
        if (task.exception is FirebaseAuthWeakPasswordException) {
            val exception = task.exception as FirebaseAuthWeakPasswordException
            if (exception.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {
                Toast.makeText(act, FirebaseAuthConstants.ERROR_WEAK_PASSWORD, Toast.LENGTH_LONG).show()
            }
        }
        if (task.exception is FirebaseAuthInvalidUserException) {
            val exception = task.exception as FirebaseAuthInvalidCredentialsException
            // Если пользователя нет с таким email
            if (exception.errorCode == FirebaseAuthConstants.ERROR_USER_NOT_FOUND) {
                Toast.makeText(act, FirebaseAuthConstants.ERROR_USER_NOT_FOUND, Toast.LENGTH_LONG).show()
            }
        }
        return false
    }

    companion object {
        const val GOOGLE_SIGN_IN_REQUEST_CODE = 132

        object FirebaseAuthConstants {
            const val ERROR_EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE"
            const val ERROR_INVALID_EMAIL = "ERROR_INVALID_EMAIL"
            const val ERROR_WRONG_PASSWORD = "ERROR_WRONG_PASSWORD"
            const val ERROR_WEAK_PASSWORD = "ERROR_WEAK_PASSWORD"
            const val ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND"
        }
    }
}