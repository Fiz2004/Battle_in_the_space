package com.fiz.android.battleinthespace.base.data

import android.content.Context
import com.fiz.android.battleinthespace.base.data.database.firebase.DBManager
import com.fiz.android.battleinthespace.base.data.database.realm.PlayerDatabaseRealm
import com.fiz.android.battleinthespace.base.data.module.PlayerRealm
import com.fiz.android.battleinthespace.base.data.storage.SharedPrefPlayerStorage
import io.realm.Realm
import io.realm.RealmConfiguration

private const val DATABASE_NAME = "BITS.realm"

class PlayerRepository(val context: Context) {
    private val config = RealmConfiguration.Builder()
        .name(DATABASE_NAME)
        .allowWritesOnUiThread(true)
        .allowQueriesOnUiThread(true)
        .build()

    val databaseRealm = Realm.getInstance(config)

    private val playersDAO = PlayerDatabaseRealm(databaseRealm)

    private val db = DBManager()

    fun fillInitValue() {
        val player1 = Player(id = 0, name = "Player 1")
        val player2 = Player(id = 1, name = "Player 2", controllerPlayer = false)
        val player3 = Player(id = 2, name = "Player 3", controllerPlayer = false)
        val player4 = Player(id = 3, name = "Player 4", controllerPlayer = false)
        addPlayer(player1)
        addPlayer(player2)
        addPlayer(player3)
        addPlayer(player4)
    }

    fun saveCountPlayers(count: Int): Boolean {
        return SharedPrefPlayerStorage(context).save(count)
    }

    fun getCountPlayers(): Int {
        return SharedPrefPlayerStorage(context).get()
    }

    fun getPlayers(): List<PlayerRealm>? = playersDAO.getAll()

    fun getPlayer(id: Int): PlayerRealm? = playersDAO.get(id)

    private fun addPlayer(player: Player) {
        playersDAO.addPlayer(player)
        //Создает уникальный ключ
        val key = db.db.push().key
        db.add(player)
    }

    fun updatePlayer(player: Player) {
        playersDAO.update(player)
    }

    fun close() {
        databaseRealm.close()
    }
}