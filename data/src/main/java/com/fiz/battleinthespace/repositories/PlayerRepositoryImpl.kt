package com.fiz.battleinthespace.repositories

import com.fiz.battleinthespace.common.Resource
import com.fiz.battleinthespace.database.ItemsDatabase
import com.fiz.battleinthespace.database.data_source.network.PlayersRemoteDataSource
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepositoryImpl @Inject constructor(
    private val playersRemoteDataSource: PlayersRemoteDataSource
) : PlayerRepository {

    override fun save(uuid: String, players: List<Player>) {
        playersRemoteDataSource.save(uuid, players)
    }

    override fun getFlowPlayers(uuid: String): Flow<Resource<List<Player>>> {
        return playersRemoteDataSource.getFlowPlayers(uuid)
    }

    override suspend fun getPlayers(uuid: String): List<Player> {
        return playersRemoteDataSource.getPlayers(uuid)
    }

    override fun initFirstLaunchPlayers(): String {
        val uuid = UUID.randomUUID().toString()

        val player1 =
            Player(id = uuid, name = "Player 1", items = ItemsDatabase.getStartItems())
        val player2 = Player(
            id = "Player 2",
            name = "Player 2",
            controllerPlayer = false,
            items = ItemsDatabase.getStartItems()
        )
        val player3 = Player(
            id = "Player 3",
            name = "Player 3",
            controllerPlayer = false,
            items = ItemsDatabase.getStartItems()
        )
        val player4 = Player(
            id = "Player 4",
            name = "Player 4",
            controllerPlayer = false,
            items = ItemsDatabase.getStartItems()
        )

        val players = listOf(player1, player2, player3, player4)

        save(uuid, players)
        return uuid
    }

    override fun initFirstLaunchGooglePlayers(uuid: String, players: List<Player>) {
        save(uuid, players)
    }
}


