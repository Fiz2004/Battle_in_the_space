package com.fiz.battleinthespace.feature_mainscreen.ui

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.battleinthespace.domain.repositories.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val textToToast: MutableSharedFlow<String?> = MutableSharedFlow()

    fun signInFirebaseWithGoogle(data: Intent) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        action {
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    return@action authRepository.signInFirebaseWithGoogle(account.idToken!!)
                } else {
                    return@action "Account equals null"
                }
            } catch (e: ApiException) {
                return@action e.message.toString()
            }
        }
    }

    private fun action(success: suspend CoroutineScope.() -> String) {
        viewModelScope.launch {
            try {
                val textStatus = success()
                textToToast.emit(textStatus)
            } catch (e: ApiException) {
                textToToast.emit(e.message.toString())
            }
        }
    }

}