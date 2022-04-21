package com.fiz.battleinthespace.feature_mainscreen.domain

import androidx.lifecycle.LiveData
import com.fiz.battleinthespace.database.models.Player

interface PlayerRepository {
    fun saveCountPlayers(count: Int): Boolean

    fun getCountPlayers(): Int

    fun getPlayers(): LiveData<List<Player>>

    suspend fun savePlayer(player: Player?)

    fun isFirstLaunch(): Boolean

    suspend fun initFirstLaunchPlayers()
}