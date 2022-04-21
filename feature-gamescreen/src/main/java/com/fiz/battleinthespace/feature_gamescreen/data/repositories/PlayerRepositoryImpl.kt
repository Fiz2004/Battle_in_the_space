package com.fiz.battleinthespace.feature_gamescreen.data.repositories

import androidx.lifecycle.LiveData
import com.fiz.battleinthespace.database.data_source.local.PlayersLocalDataSource
import com.fiz.battleinthespace.database.data_source.local.SharedPrefPlayerStorage
import com.fiz.battleinthespace.database.models.Player
import com.fiz.battleinthespace.feature_gamescreen.domain.PlayerRepository

class PlayerRepositoryImpl(
    private val playersLocalDataSource: PlayersLocalDataSource,
    private val sharedPrefPlayerStorage: SharedPrefPlayerStorage
) : PlayerRepository {
    override fun getCountPlayers(): Int {
        return sharedPrefPlayerStorage.getCountPlayers()
    }

    override fun getPlayers(): LiveData<List<Player>> = playersLocalDataSource.getAll()

    override suspend fun save(player: Player?) {
        if (player == null) return
        playersLocalDataSource.save(player)
    }
}