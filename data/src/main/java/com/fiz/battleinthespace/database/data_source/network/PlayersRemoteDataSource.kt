package com.fiz.battleinthespace.database.data_source.network

import com.fiz.battleinthespace.database.firebase.DBManager
import com.fiz.battleinthespace.domain.models.Player

class PlayersRemoteDataSource {
    private val db = DBManager()

    private suspend fun addPlayer(player: Player) {
        val key = db.db.push().key
        db.add(player)
    }
}