package com.fiz.battleinthespace.domain.usecase

import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlayersFlowUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val getUuidUseCase: GetUuidUseCase,
) {

    suspend operator fun invoke(): Flow<Result<List<Player>>> {
        return playerRepository.getFlowPlayers(getUuidUseCase())
    }
}