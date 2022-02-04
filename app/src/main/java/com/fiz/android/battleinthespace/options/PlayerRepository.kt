package com.fiz.android.battleinthespace.options

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.fiz.android.battleinthespace.database.PlayerDatabase
import java.util.concurrent.Executors

private const val DATABASE_NAME = "player-database"

class PlayerRepository private constructor(context: Context) {

    private val database: PlayerDatabase = Room.databaseBuilder(
        context.applicationContext,
        PlayerDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val playersDAO = DataBaseFake()

    private val playersFromDatabaseDAO = database.playerDao()

    private val countPlayer = playersDAO.getCount()

    fun getCountPlayer() = countPlayer

    fun getPlayers() = MutableLiveData<List<Player>>(playersDAO.getAll())

    fun getPlayer(number: Int) = playersDAO.get(number)

    fun addPlayer(player: Player) {
        Executors.newSingleThreadExecutor().execute {
            playersFromDatabaseDAO.addPlayer(player)
        }
    }

    fun updatePlayer(player: Player) {
        Executors.newSingleThreadExecutor().execute {
            playersFromDatabaseDAO.update(player)
        }
    }

    companion object {
        private var INSTANCE: PlayerRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = PlayerRepository(context)
            }
        }

        fun get(): PlayerRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}

class DataBaseFake {
    private val databasePlayers = mutableListOf(
        Player(id = 0, name = "Player 1"),
        Player(id = 1, name = "Player 2", controllerPlayer = false),
        Player(id = 2, name = "Player 3", controllerPlayer = false),
        Player(id = 3, name = "Player 4", controllerPlayer = false))

    fun getCount(): Int {
        return databasePlayers.size
    }

    fun getAll(): List<Player> {
        return databasePlayers
    }

    fun get(id: Int): LiveData<Player>? {
        return MutableLiveData<Player>(databasePlayers[id])
    }
}