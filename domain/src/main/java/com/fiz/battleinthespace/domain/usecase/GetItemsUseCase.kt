package com.fiz.battleinthespace.domain.usecase

import com.fiz.battleinthespace.domain.models.SubItem
import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import javax.inject.Inject

class GetItemsUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val getUuidUseCase: GetUuidUseCase,
) {

    suspend operator fun invoke(categoryId: String): List<SubItem> {
        return SubItem.addZeroFirstItem(
            playerRepository.getItems(getUuidUseCase(), categoryId)
        )
    }
}