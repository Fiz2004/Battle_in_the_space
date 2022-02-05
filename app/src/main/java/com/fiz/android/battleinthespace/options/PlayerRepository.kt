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

    private val playersDAO = database.playerDao()

    init {
        Executors.newSingleThreadExecutor().execute {
            playersDAO.clear()
        }
        if (playersDAO.getCount().value == 0 || playersDAO.getCount().value == null) {
            val player1 = Player(id = 0, name = "Player 1")
            val player2 = Player(id = 1, name = "Player 2", controllerPlayer = false)
            val player3 = Player(id = 2, name = "Player 3", controllerPlayer = false)
            val player4 = Player(id = 3, name = "Player 4", controllerPlayer = false)
            addPlayer(player1)
            addPlayer(player2)
            addPlayer(player3)
            addPlayer(player4)
        }
    }

    private val countPlayer = playersDAO.getCount()

    fun getCountPlayer() = countPlayer

    fun getPlayers(): LiveData<List<Player>> {
        val result = playersDAO.getAll()
        return result
    }

    fun getPlayer(id: Int) = playersDAO.get(id)

    fun addPlayer(player: Player) {
        Executors.newSingleThreadExecutor().execute {
            playersDAO.addPlayer(player)
        }
    }

    fun updatePlayer(player: Player) {
        Executors.newSingleThreadExecutor().execute {
            playersDAO.update(player)
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
        Player(id = 3, name = "Player 4", controllerPlayer = false)
    )

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