package com.fiz.battleinthespace.database.data_source.network

import com.fiz.battleinthespace.domain.models.CategoryItem
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.domain.models.StateProduct
import com.fiz.battleinthespace.domain.models.SubItem
import kotlinx.coroutines.flow.Flow

internal interface PlayersRemoteDataSource {

    suspend fun save(uuid: String, players: List<Player>)

    fun getFlowPlayers(uuid: String): Flow<Result<List<Player>>>

    suspend fun getPlayers(uuid: String): List<Player>

    suspend fun getItems(uuid: String, categoryId: String): List<SubItem>
    suspend fun getCategoryItems(uuid: String): List<CategoryItem>
    suspend fun saveMission(uuid: String, value: Int)
    suspend fun setItemState(uuid: String, item: SubItem.Item, value: StateProduct)
    suspend fun buyItemState(uuid: String, item: SubItem.Item)
    suspend fun setName(uuid: String, index: Int, newName: String)
    suspend fun setController(uuid: String, index: Int, checked: Boolean)
}