package com.fiz.battleinthespace.domain.usecase

import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import javax.inject.Inject

class SetMissionUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val getUuidUseCase: GetUuidUseCase,
) {

    suspend operator fun invoke(value: Int) {
        playerRepository.saveMission(getUuidUseCase(), value)
    }
}