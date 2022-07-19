package com.fiz.battleinthespace.repositories

import androidx.lifecycle.LiveData
import com.fiz.battleinthespace.database.ItemsDatabase
import com.fiz.battleinthespace.database.data_source.local.PlayersLocalDataSource
import com.fiz.battleinthespace.database.data_source.local.SharedPrefPlayerStorage
import com.fiz.battleinthespace.database.data_source.network.PlayersRemoteDataSource
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.domain.repositories.PlayerRepository
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val playersLocalDataSource: PlayersLocalDataSource,
    private val sharedPrefPlayerStorage: SharedPrefPlayerStorage
) : PlayerRepository {

    override suspend fun save(player: Player?) {
        if (player == null) return
        playersLocalDataSource.save(player)
        PlayersRemoteDataSource().addPlayer(player)
    }

    override fun saveCountPlayers(count: Int): Boolean {
        return sharedPrefPlayerStorage.saveCountPlayers(count)
    }

    override fun getCountPlayers(): Int {
        return sharedPrefPlayerStorage.getCountPlayers()
    }

    override fun getObservePlayers(): LiveData<List<Player>> =
        playersLocalDataSource.getObserveAll()

    override fun getPlayers(): List<Player> = playersLocalDataSource.getAll()

    private fun addPlayer(player: Player) {
        playersLocalDataSource.addPlayer(player)
    }

    override suspend fun savePlayer(player: Player?) {
        if (player == null) return
        playersLocalDataSource.save(player)
    }

    override fun isFirstLaunch(): Boolean {
        return sharedPrefPlayerStorage.loadIsFirstLaunch()
    }

    override suspend fun initFirstLaunchPlayers() {
        val player1 = Player(id = 0, name = "Player 1", items = ItemsDatabase.getStartItems())
        val player2 = Player(
            id = 1,
            name = "Player 2",
            controllerPlayer = false,
            items = ItemsDatabase.getStartItems()
        )
        val player3 = Player(
            id = 2,
            name = "Player 3",
            controllerPlayer = false,
            items = ItemsDatabase.getStartItems()
        )
        val player4 = Player(
            id = 3,
            name = "Player 4",
            controllerPlayer = false,
            items = ItemsDatabase.getStartItems()
        )

        addPlayer(player1)
        addPlayer(player2)
        addPlayer(player3)
        addPlayer(player4)

        sharedPrefPlayerStorage.saveIsFirstLaunchComplete()
    }
}