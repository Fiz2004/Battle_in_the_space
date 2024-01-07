package com.fiz.battleinthespace.domain.usecase

import com.fiz.battleinthespace.domain.repositories.AuthRepository
import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import com.fiz.battleinthespace.domain.repositories.SettingsRepository
import javax.inject.Inject

class GetUuidUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val playerRepository: PlayerRepository,
    private val settingsRepository: SettingsRepository,
) {

    suspend operator fun invoke(): String {
        val guuid = authRepository.getAuthUuid()
        if (guuid != null) {
            settingsRepository.saveAuthUuid(guuid)
            return guuid
        }
        val uuid = settingsRepository.getUuid()
        if (uuid == null) {
            val newUuid = playerRepository.initFirstLaunchPlayers()
            settingsRepository.saveAuthUuid(newUuid)
            return newUuid
        } else {
            return uuid
        }
    }
}