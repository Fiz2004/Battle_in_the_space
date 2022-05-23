package com.fiz.battleinthespace.domain.repositories

import androidx.lifecycle.LiveData
import com.fiz.battleinthespace.domain.models.Player

interface PlayerRepository {
    fun saveCountPlayers(count: Int): Boolean

    fun getCountPlayers(): Int

    fun getPlayers(): LiveData<List<Player>>

    suspend fun savePlayer(player: Player?)

    fun isFirstLaunch(): Boolean

    suspend fun initFirstLaunchPlayers()

    suspend fun save(player: Player?)
}