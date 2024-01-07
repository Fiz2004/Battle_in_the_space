package com.fiz.battleinthespace.domain.usecase

import com.fiz.battleinthespace.domain.repositories.SettingsRepository
import javax.inject.Inject

class SetCountPlayersUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {

    suspend operator fun invoke(numberRadioButton: Int) {
        settingsRepository.saveCountPlayers(numberRadioButton)
    }
}