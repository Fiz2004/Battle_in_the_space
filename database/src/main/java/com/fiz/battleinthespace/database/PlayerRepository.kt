package com.fiz.battleinthespace.database

import androidx.lifecycle.LiveData
import com.fiz.battleinthespace.database.data_source.local.PlayersLocalDataSource
import com.fiz.battleinthespace.database.firebase.DBManager
import com.fiz.battleinthespace.database.module.PlayerRealm
import com.fiz.battleinthespace.database.storage.SharedPrefPlayerStorage

class PlayerRepository(
    private val playersLocalDataSource: PlayersLocalDataSource,
    private val sharedPrefPlayerStorage: SharedPrefPlayerStorage
) {

    private val db = DBManager()

    fun fillInitValue() {
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
    }

    fun saveCountPlayers(count: Int): Boolean {
        return sharedPrefPlayerStorage.save(count)
    }

    fun getCountPlayers(): Int {
        return sharedPrefPlayerStorage.get()
    }

    fun getPlayers(): LiveData<List<Player>> = playersLocalDataSource.getAll()

    fun getPlayer(id: Int): PlayerRealm? = playersLocalDataSource.get(id)

    private fun addPlayer(player: Player) {
        playersLocalDataSource.addPlayer(player)
        //Создает уникальный ключ
        val key = db.db.push().key
        db.add(player)
    }

    fun updatePlayer(player: Player?) {
        if (player == null) return
        playersLocalDataSource.update(player)
    }

    fun close() {
        playersLocalDataSource.close()
    }

    fun save(player: Player?) {
        if (player == null) return
        playersLocalDataSource.save(player)
    }
}