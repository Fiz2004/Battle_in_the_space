package com.fiz.battleinthespace.feature_gamescreen.domain

import androidx.lifecycle.LiveData
import com.fiz.battleinthespace.database.models.Player

interface PlayerRepository {
    fun getCountPlayers(): Int

    fun getPlayers(): LiveData<List<Player>>

    suspend fun save(player: Player?)
}