package com.fiz.battleinthespace.database.data_source.network

import com.fiz.battleinthespace.database.firebase.DBManager
import com.fiz.battleinthespace.domain.models.Player

class PlayersRemoteDataSource {
    private val db = DBManager()

    suspend fun addPlayer(player: Player) {
        db.add(player)
    }
}