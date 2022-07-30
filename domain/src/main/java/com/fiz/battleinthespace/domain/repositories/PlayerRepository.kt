package com.fiz.battleinthespace.domain.repositories

import com.fiz.battleinthespace.common.Resource
import com.fiz.battleinthespace.domain.models.Player
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {

    fun getFlowPlayers(uuid: String): Flow<Resource<List<Player>>>

    suspend fun getPlayers(uuid: String): List<Player>

    fun save(uuid: String, players: List<Player>)

    fun initFirstLaunchPlayers(): String
    fun initFirstLaunchGooglePlayers(uuid: String, players: List<Player>)
}

