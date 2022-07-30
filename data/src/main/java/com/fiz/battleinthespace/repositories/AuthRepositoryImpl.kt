package com.fiz.battleinthespace.repositories

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class AuthRepositoryImpl @Inject constructor() {

    val mAuth: FirebaseAuth = Firebase.auth
    val user: MutableStateFlow<FirebaseUser?> = MutableStateFlow(mAuth.currentUser)
    val email: MutableStateFlow<String?> = MutableStateFlow(mAuth.currentUser?.email)

    init {
        user.onEach {
            email.value = it?.email
        }.launchIn(CoroutineScope(Dispatchers.Default))
    }

    suspend fun createAccount(email: String, password: String): String {
        try {
            val firebaseUser = suspendCoroutine { continuation ->
                if (email.isEmpty() || password.isEmpty())
                    continuation.resumeWithException(Exception("Email или пароль пустые"))

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            task.result?.user?.let { firebaseUser ->
                                continuation.resumeWith(Result.success(firebaseUser))
                            } ?: continuation.resumeWithException(Exception("USER равен null"))
                        } else {
                            continuation.resumeWithException(Exception(task.exception))
                        }
                    }
            }
            return sendEmailVerification(firebaseUser)
        } catch (e: Exception) {
            if (e is FirebaseAuthUserCollisionException) {
                if (e.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                    return try {
                        linkEmailToG(email, password)
                    } catch (e: Exception) {
                        return e.message.toString()
                    }
                }
            }
            return e.message.toString()
        }
    }


    private suspend fun sendEmailVerification(user: FirebaseUser): String =
        suspendCoroutine { continuation ->
            user.sendEmailVerification()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        this.user.value = user
                        continuation.resumeWith(Result.success("Email для подтверждения отправлен"))
                    } else {
                        continuation.resumeWithException(Exception("Ошибка при отправке email"))
                    }
                }
        }

    private suspend fun linkEmailToG(email: String, password: String): String =
        suspendCoroutine { continuation ->
            val credential = EmailAuthProvider.getCredential(email, password)
            mAuth.currentUser?.linkWithCredential(credential)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resumeWith(Result.success("EMAIL_ALREADY_IN_USE AND Link ok"))
                    } else {
                        continuation.resumeWithException(Exception(it.exception))
                    }
                }
        }


    suspend fun signInWithEmail(email: String, password: String): String {
        try {
            val firebaseUser = suspendCoroutine { continuation ->
                if (email.isEmpty() || password.isEmpty())
                    continuation.resumeWithException(Exception("EMAIL OR PASSWORD EMPTY"))

                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            task.result?.user?.let { firebaseUser ->
                                continuation.resumeWith(Result.success(firebaseUser))
                            } ?: continuation.resumeWithException(Exception("USER EQUALS NULL"))
                        } else {
                            continuation.resumeWithException(Exception(task.exception))
                        }
                    }
            }
            user.value = firebaseUser
            return "Ok"
        } catch (e: Exception) {
            return e.message.toString()
        }
    }

    suspend fun signInFirebaseWithGoogle(result: Intent): String {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                return suspendCoroutine { continuation ->
                    mAuth.signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                user.value = it.result?.user
                                continuation.resumeWith(Result.success("Вход выполнен"))
                            } else {
                                continuation.resumeWithException(Exception("Ошибка входа"))
                            }
                        }
                }
            } else {
                return "Account equals null"
            }
        } catch (e: ApiException) {
            return e.message.toString()
        }
    }

    suspend fun resetPassword(email: String): String = suspendCoroutine { continuation ->
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resumeWith(Result.success("Email был отправлен"))
                } else {
                    continuation.resumeWithException(Exception("Ошибка при отправке email"))
                }
            }
    }

    private fun printInfoExceptionAndResolveProcess(task: Task<AuthResult>): String {
        if (task.exception is FirebaseAuthUserCollisionException) {
            val exception = task.exception as FirebaseAuthUserCollisionException
            if (exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                return (FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE)
            }
        }
        if (task.exception is FirebaseAuthInvalidCredentialsException) {
            val exception = task.exception as FirebaseAuthInvalidCredentialsException
            if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                return (FirebaseAuthConstants.ERROR_INVALID_EMAIL)
            }
            if (exception.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                return (FirebaseAuthConstants.ERROR_WRONG_PASSWORD)
            }
        }
        if (task.exception is FirebaseAuthWeakPasswordException) {
            val exception = task.exception as FirebaseAuthWeakPasswordException
            if (exception.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {
                return (FirebaseAuthConstants.ERROR_WEAK_PASSWORD)
            }
        }
        if (task.exception is FirebaseAuthInvalidUserException) {
            val exception = task.exception as FirebaseAuthInvalidCredentialsException
            // Если пользователя нет с таким email
            if (exception.errorCode == FirebaseAuthConstants.ERROR_USER_NOT_FOUND) {
                return (FirebaseAuthConstants.ERROR_USER_NOT_FOUND)
            }
        }
        return ""
    }

    fun signOut() {
        user.value = null
        mAuth.signOut()
    }

    fun getFlowEmail(): MutableStateFlow<String?> {
        return email
    }

    fun getAuthUuid(): String? {
        return user.value?.uid
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