package com.fiz.battleinthespace.repositories

import com.fiz.battleinthespace.database.ItemsDatabase
import com.fiz.battleinthespace.database.data_source.network.PlayersRemoteDataSource
import com.fiz.battleinthespace.domain.models.CategoryItem
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.domain.models.StateProduct
import com.fiz.battleinthespace.domain.models.SubItem
import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PlayerRepositoryImpl @Inject constructor(
    private val playersRemoteDataSource: PlayersRemoteDataSource
) : PlayerRepository {

    override suspend fun save(uuid: String, players: List<Player>) {
        playersRemoteDataSource.save(uuid, players)
    }

    override fun getFlowPlayers(uuid: String): Flow<Result<List<Player>>> {
        return playersRemoteDataSource.getFlowPlayers(uuid)
    }

    override suspend fun setItemState(uuid: String, item: SubItem.Item, value: StateProduct) {
        playersRemoteDataSource.setItemState(uuid, item, value)
    }

    override suspend fun getPlayers(uuid: String): List<Player> {
        return playersRemoteDataSource.getPlayers(uuid)
    }

    override suspend fun initFirstLaunchPlayers(): String {
        val uuid = UUID.randomUUID().toString()

        val player1 =
            Player(id = uuid, name = "Player 1", categoryItems = ItemsDatabase.getStartItems())
        val player2 = Player(
            id = "Player 2",
            name = "Player 2",
            controllerPlayer = false,
            categoryItems = ItemsDatabase.getStartItems()
        )
        val player3 = Player(
            id = "Player 3",
            name = "Player 3",
            controllerPlayer = false,
            categoryItems = ItemsDatabase.getStartItems()
        )
        val player4 = Player(
            id = "Player 4",
            name = "Player 4",
            controllerPlayer = false,
            categoryItems = ItemsDatabase.getStartItems()
        )

        val players = listOf(player1, player2, player3, player4)

        save(uuid, players)
        return uuid
    }

    override suspend fun buyItemState(uuid: String, item: SubItem.Item) {
        playersRemoteDataSource.buyItemState(uuid, item)
    }

    override suspend fun setName(uuid: String, index: Int, newName: String) {
        playersRemoteDataSource.setName(uuid, index, newName)
    }

    override suspend fun setController(uuid: String, index: Int, checked: Boolean) {
        playersRemoteDataSource.setController(uuid, index, checked)
    }

    override suspend fun resetPlayer(uuid: String, number: Int) {
        val players = getPlayers(uuid).toMutableList()
        players[number] = when (number) {
            0 -> Player(id = uuid, name = "Player 1", categoryItems = ItemsDatabase.getStartItems())
            1 -> Player(
                id = "Player 2",
                name = "Player 2",
                controllerPlayer = false,
                categoryItems = ItemsDatabase.getStartItems()
            )

            2 -> Player(
                id = "Player 3",
                name = "Player 3",
                controllerPlayer = false,
                categoryItems = ItemsDatabase.getStartItems()
            )

            else -> Player(
                id = "Player 4",
                name = "Player 4",
                controllerPlayer = false,
                categoryItems = ItemsDatabase.getStartItems()
            )
        }

        save(uuid, players)
    }

    override suspend fun getItems(uuid: String, categoryId: String): List<SubItem> {
        return playersRemoteDataSource.getItems(uuid, categoryId)
    }

    override suspend fun getCategoryItems(uuid: String): List<CategoryItem> {
        return playersRemoteDataSource.getCategoryItems(uuid)
    }

    override suspend fun saveMission(uuid: String, value: Int) {
        playersRemoteDataSource.saveMission(uuid, value)
    }

    override suspend fun initFirstLaunchGooglePlayers(uuid: String, players: List<Player>) {
        save(uuid, players)
    }
}


