package com.fiz.battleinthespace.domain.usecase

import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import javax.inject.Inject

class SetNameUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val getUuidUseCase: GetUuidUseCase,
) {

    suspend operator fun invoke(index: Int, newName: String) {
        playerRepository.setName(getUuidUseCase(), index, newName)
    }
}