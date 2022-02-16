package com.fiz.android.battleinthespace.base.domain.accounthelper

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.presentation.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*

class AccountHelper {
    val mAuth = FirebaseAuth.getInstance()
    var user = MutableLiveData<FirebaseUser?>(null)

    private var errorTextToToast = MutableLiveData<String?>(null)

    fun signUpWithEmail(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) return
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    sendEmailVerification(it.result?.user!!)
                } else {
                    if (printInfoExceptionAndResolveProcess(it))
                        linkEmailToG(email, password)
                }
            }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    setErrorTextToToast("send email")
                    this.user.value = user
                } else {
                    setErrorTextToToast("Error send email")
                }
            }
    }

    fun signInFirebaseWithGoogle(result: Intent) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                if (mAuth != null) {
                    mAuth.signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                setErrorTextToToast("Sign in done")
                                user.value = it.result?.user
                            } else {
                                Log.d("MyLog", "Google sign in exception: ${it.exception}")
                                setErrorTextToToast("Error Sign in done")
                            }
                        }
                } else {
                    setErrorTextToToast("У вас уже есть аккаунт с таким email, войдите сначала через почту")
                }
            } else {
            }
        } catch (e: ApiException) {
            Log.d("MyLog", "Api error ${e.message}")
        }
    }

    private fun linkEmailToG(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        mAuth.currentUser?.linkWithCredential(credential)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    setErrorTextToToast("Link ok")
                } else {
                    printInfoExceptionAndResolveProcess(it)
                }
            }
    }

    private fun printInfoExceptionAndResolveProcess(task: Task<AuthResult>): Boolean {
        if (task.exception is FirebaseAuthUserCollisionException) {
            val exception = task.exception as FirebaseAuthUserCollisionException
            if (exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                setErrorTextToToast(FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE)
                return true
            }
        }
        if (task.exception is FirebaseAuthInvalidCredentialsException) {
            val exception = task.exception as FirebaseAuthInvalidCredentialsException
            if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                setErrorTextToToast(FirebaseAuthConstants.ERROR_INVALID_EMAIL)
            }
            if (exception.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                setErrorTextToToast(FirebaseAuthConstants.ERROR_WRONG_PASSWORD)
            }
        }
        if (task.exception is FirebaseAuthWeakPasswordException) {
            val exception = task.exception as FirebaseAuthWeakPasswordException
            if (exception.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {
                setErrorTextToToast(FirebaseAuthConstants.ERROR_WEAK_PASSWORD)
            }
        }
        if (task.exception is FirebaseAuthInvalidUserException) {
            val exception = task.exception as FirebaseAuthInvalidCredentialsException
            // Если пользователя нет с таким email
            if (exception.errorCode == FirebaseAuthConstants.ERROR_USER_NOT_FOUND) {
                setErrorTextToToast(FirebaseAuthConstants.ERROR_USER_NOT_FOUND)
            }
        }
        return false
    }

    fun resetPassword(email: String) {
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    setErrorTextToToast("email was sent")
                }
            }
    }

    fun signInWithGoogle(act: MainActivity) {
        val signInClient = getSignInClient(act)
        val intent = signInClient.signInIntent
        act.googleSignInActivityLauncher.launch(intent)
    }

    fun signInOutG(act: MainActivity) {
        user.value = null
        mAuth.signOut()
        getSignInClient(act).signOut()
    }

    //TODO Разобраться почему R.string не найден classpath 'com.google.gms:google-services:4.3.10'
    private fun getSignInClient(act: MainActivity): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(act, gso)
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) return
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    user.value = it.result?.user!!
                } else {
                    printInfoExceptionAndResolveProcess(it)
                }
            }
    }

    fun initErrorTextToToast(errorTextToToast: MutableLiveData<String?>) {
        this.errorTextToToast = errorTextToToast
    }

    private fun setErrorTextToToast(value: String) {
        errorTextToToast.value = value
    }

    companion object {
        object FirebaseAuthConstants {
            const val ERROR_EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE"
            const val ERROR_INVALID_EMAIL = "ERROR_INVALID_EMAIL"
            const val ERROR_WRONG_PASSWORD = "ERROR_WRONG_PASSWORD"
            const val ERROR_WEAK_PASSWORD = "ERROR_WEAK_PASSWORD"
            const val ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND"
        }
    }
}