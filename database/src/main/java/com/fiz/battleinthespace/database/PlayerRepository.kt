package com.fiz.battleinthespace.database

import androidx.lifecycle.LiveData
import com.fiz.battleinthespace.database.data_source.local.PlayersLocalDataSource
import com.fiz.battleinthespace.database.storage.SharedPrefPlayerStorage

class PlayerRepository(
    private val playersLocalDataSource: PlayersLocalDataSource,
    private val sharedPrefPlayerStorage: SharedPrefPlayerStorage
) {
    fun saveCountPlayers(count: Int): Boolean {
        return sharedPrefPlayerStorage.saveCountPlayers(count)
    }

    fun getCountPlayers(): Int {
        return sharedPrefPlayerStorage.getCountPlayers()
    }

    fun getPlayers(): LiveData<List<Player>> = playersLocalDataSource.getAll()

    fun getPlayer(id: Int): Player? = playersLocalDataSource.get(id)

    private suspend fun addPlayer(player: Player) {
        playersLocalDataSource.addPlayer(player)
    }

    suspend fun updatePlayer(player: Player?) {
        if (player == null) return
        playersLocalDataSource.update(player)
    }

    fun close() {
        playersLocalDataSource.close()
    }

    suspend fun save(player: Player?) {
        if (player == null) return
        playersLocalDataSource.save(player)
    }

    fun isFirstLaunch(): Boolean {
        return sharedPrefPlayerStorage.loadIsFirstLaunch()
    }

    suspend fun initFirstLaunchPlayers() {
        val player1 = Player(id = 0, name = "Player 1")
        val player2 = Player(
            id = 1,
            name = "Player 2",
            controllerPlayer = false
        )
        val player3 = Player(
            id = 2,
            name = "Player 3",
            controllerPlayer = false
        )
        val player4 = Player(
            id = 3,
            name = "Player 4",
            controllerPlayer = false
        )

        addPlayer(player1)
        addPlayer(player2)
        addPlayer(player3)
        addPlayer(player4)

        sharedPrefPlayerStorage.saveIsFirstLaunchComplete()
    }
}