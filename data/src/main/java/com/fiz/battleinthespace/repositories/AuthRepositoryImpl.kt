package com.fiz.battleinthespace.repositories

import com.fiz.battleinthespace.domain.repositories.AuthRepository
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
internal class AuthRepositoryImpl @Inject constructor(
    private val mAuth: FirebaseAuth
) : AuthRepository {

    private val user: MutableStateFlow<FirebaseUser?> = MutableStateFlow(mAuth.currentUser)
    private val email: MutableStateFlow<String?> = MutableStateFlow(mAuth.currentUser?.email)

    init {
        user.onEach { user ->
            email.value = user?.email
        }.launchIn(CoroutineScope(Dispatchers.Default))
    }

    override suspend fun createAccount(email: String, password: String): String {
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
                if (e.errorCode == ERROR_EMAIL_ALREADY_IN_USE) {
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


    override suspend fun signInWithEmail(email: String, password: String): String {
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

    override suspend fun signInFirebaseWithGoogle(result: String): String {
        try {
            val credential = GoogleAuthProvider.getCredential(result, null)
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
        } catch (e: ApiException) {
            return e.message.toString()
        }
    }

    override suspend fun resetPassword(email: String): String = suspendCoroutine { continuation ->
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
            if (exception.errorCode == ERROR_EMAIL_ALREADY_IN_USE) {
                return (ERROR_EMAIL_ALREADY_IN_USE)
            }
        }
        if (task.exception is FirebaseAuthInvalidCredentialsException) {
            val exception = task.exception as FirebaseAuthInvalidCredentialsException
            if (exception.errorCode == ERROR_INVALID_EMAIL) {
                return (ERROR_INVALID_EMAIL)
            }
            if (exception.errorCode == ERROR_WRONG_PASSWORD) {
                return (ERROR_WRONG_PASSWORD)
            }
        }
        if (task.exception is FirebaseAuthWeakPasswordException) {
            val exception = task.exception as FirebaseAuthWeakPasswordException
            if (exception.errorCode == ERROR_WEAK_PASSWORD) {
                return (ERROR_WEAK_PASSWORD)
            }
        }
        if (task.exception is FirebaseAuthInvalidUserException) {
            val exception = task.exception as FirebaseAuthInvalidCredentialsException
            // Если пользователя нет с таким email
            if (exception.errorCode == ERROR_USER_NOT_FOUND) {
                return (ERROR_USER_NOT_FOUND)
            }
        }
        return ""
    }

    override fun signOut() {
        user.value = null
        mAuth.signOut()
    }

    override fun getFlowEmail(): Flow<String?> {
        return email
    }

    override fun getAuthUuid(): String? {
        return user.value?.uid
    }

    private companion object {
        const val ERROR_EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE"
        const val ERROR_INVALID_EMAIL = "ERROR_INVALID_EMAIL"
        const val ERROR_WRONG_PASSWORD = "ERROR_WRONG_PASSWORD"
        const val ERROR_WEAK_PASSWORD = "ERROR_WEAK_PASSWORD"
        const val ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND"
    }
}