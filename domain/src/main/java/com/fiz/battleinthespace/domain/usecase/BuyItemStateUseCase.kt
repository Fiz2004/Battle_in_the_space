package com.fiz.battleinthespace.domain.usecase

import com.fiz.battleinthespace.domain.models.SubItem
import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import javax.inject.Inject

class BuyItemStateUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val getUuidUseCase: GetUuidUseCase,
) {

    suspend operator fun invoke(item: SubItem.Item) {
        playerRepository.buyItemState(getUuidUseCase(), item)
    }
}