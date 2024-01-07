package com.fiz.battleinthespace.domain.repositories

import com.fiz.battleinthespace.domain.models.CategoryItem
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.domain.models.StateProduct
import com.fiz.battleinthespace.domain.models.SubItem
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {

    fun getFlowPlayers(uuid: String): Flow<Result<List<Player>>>

    suspend fun getPlayers(uuid: String): List<Player>

    suspend fun save(uuid: String, players: List<Player>)

    suspend fun initFirstLaunchPlayers(): String
    suspend fun initFirstLaunchGooglePlayers(uuid: String, players: List<Player>)
    suspend fun resetPlayer(uuid: String, number: Int)

    suspend fun getItems(uuid: String, categoryId: String): List<SubItem>
    suspend fun getCategoryItems(uuid: String): List<CategoryItem>
    suspend fun saveMission(uuid: String, value: Int)
    suspend fun setItemState(uuid: String, item: SubItem.Item, value: StateProduct)
    suspend fun buyItemState(uuid: String, item: SubItem.Item)
    suspend fun setName(uuid: String, index: Int, newName: String)
    suspend fun setController(uuid: String, index: Int, checked: Boolean)
}

