package com.fiz.battleinthespace.domain.usecase

import com.fiz.battleinthespace.domain.repositories.AuthRepository
import com.fiz.battleinthespace.domain.repositories.SettingsRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
) {

    suspend operator fun invoke() {
        settingsRepository.deleteAuthUuid()
        authRepository.signOut()
    }
}