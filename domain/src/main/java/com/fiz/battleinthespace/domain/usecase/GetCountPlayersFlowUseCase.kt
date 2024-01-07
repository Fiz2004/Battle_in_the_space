package com.fiz.battleinthespace.domain.usecase

import com.fiz.battleinthespace.domain.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCountPlayersFlowUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {

    operator fun invoke(): Flow<Result<Int>> {
        return settingsRepository.getFlowCountPlayers()
    }
}