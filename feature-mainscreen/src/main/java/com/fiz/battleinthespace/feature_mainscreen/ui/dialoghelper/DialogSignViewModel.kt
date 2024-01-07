package com.fiz.battleinthespace.feature_mainscreen.ui.dialoghelper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.battleinthespace.domain.repositories.AuthRepository
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DialogSignViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    val textToToast: MutableSharedFlow<String?> = MutableSharedFlow()

    fun signUpWithEmail(email: String, password: String) {
        action { authRepository.createAccount(email, password) }
    }

    fun signInWithEmail(email: String, password: String) {
        action { authRepository.signInWithEmail(email, password) }
    }

    fun resetPassword(email: String) {
        action { authRepository.resetPassword(email) }
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