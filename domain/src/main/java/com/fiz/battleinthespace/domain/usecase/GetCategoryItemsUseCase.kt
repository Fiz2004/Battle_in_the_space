package com.fiz.battleinthespace.domain.usecase

import com.fiz.battleinthespace.domain.models.CategoryItem
import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import javax.inject.Inject

class GetCategoryItemsUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val getUuidUseCase: GetUuidUseCase,
) {

    suspend operator fun invoke(): List<CategoryItem> {
        return playerRepository.getCategoryItems(getUuidUseCase())
    }
}