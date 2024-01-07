package com.fiz.battleinthespace.domain.usecase

import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import javax.inject.Inject

class SetControllerUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val getUuidUseCase: GetUuidUseCase,
) {

    suspend operator fun invoke(index: Int, isChecked: Boolean) {
        playerRepository.setController(getUuidUseCase(), index, isChecked)
    }
}