package com.fiz.battleinthespace.domain.usecase

import com.fiz.battleinthespace.domain.models.StateProduct
import com.fiz.battleinthespace.domain.models.SubItem
import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import javax.inject.Inject

class SetItemStateUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val getUuidUseCase: GetUuidUseCase,
) {

    suspend operator fun invoke(item: SubItem.Item, value: StateProduct) {
        playerRepository.setItemState(getUuidUseCase(), item, value)
    }
}