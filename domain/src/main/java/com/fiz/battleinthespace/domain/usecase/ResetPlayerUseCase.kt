package com.fiz.battleinthespace.domain.usecase

import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import javax.inject.Inject

class ResetPlayerUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val getUuidUseCase: GetUuidUseCase,
) {

    suspend operator fun invoke(number: Int) {
        playerRepository.resetPlayer(getUuidUseCase(), number)
    }
}