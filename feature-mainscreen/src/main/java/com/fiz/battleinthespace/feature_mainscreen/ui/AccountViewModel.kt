package com.fiz.battleinthespace.feature_mainscreen.ui

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import com.fiz.battleinthespace.domain.repositories.SettingsRepository
import com.fiz.battleinthespace.repositories.AuthRepositoryImpl
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val settingsRepository: SettingsRepository,
    private val playerRepository: PlayerRepository
) :
    ViewModel() {

    val email: MutableStateFlow<String?> = MutableStateFlow(null)
    val textToToast: MutableSharedFlow<String?> = MutableSharedFlow()

    init {
        authRepository.getFlowEmail()
            .onEach(::actionFlowEmail)
            .launchIn(viewModelScope)
    }

    private suspend fun actionFlowEmail(email: String?) {
        this@AccountViewModel.email.value = email
        email ?: return
        val uuid = authRepository.getAuthUuid() ?: return

        try {
            playerRepository.getPlayers(uuid)
            settingsRepository.saveAuthUuid(uuid)
        } catch (e: Exception) {
            val players = playerRepository.getPlayers(
                settingsRepository.getFlowUuid().first()
            )
            playerRepository.initFirstLaunchGooglePlayers(uuid, players)
        }
    }

    fun signUpWithEmail(email: String, password: String) {
        action { authRepository.createAccount(email, password) }
    }

    fun signInWithEmail(email: String, password: String) {
        action { authRepository.signInWithEmail(email, password) }
    }

    fun signInFirebaseWithGoogle(data: Intent) {
        action { authRepository.signInFirebaseWithGoogle(data) }
    }

    fun resetPassword(email: String) {
        action { authRepository.resetPassword(email) }
    }

    fun signInOutG() {
        settingsRepository.deleteAuthUuid()
        authRepository.signOut()
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