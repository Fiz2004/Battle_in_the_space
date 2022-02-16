package com.fiz.android.battleinthespace.base.data

import android.content.Context
import androidx.lifecycle.LiveData
import com.fiz.android.battleinthespace.base.data.database.PlayerDao
import com.fiz.android.battleinthespace.base.data.storage.SharedPrefPlayerStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(@ApplicationContext val context: Context) {

    @Inject
    lateinit var playersDao: PlayerDao

    fun fillInitValue() {
        Executors.newSingleThreadExecutor().execute {
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

    fun saveCountPlayers(count: Int): Boolean {
        return SharedPrefPlayerStorage(context).save(count)
    }

    fun getCountPlayers(): Int {
        return SharedPrefPlayerStorage(context).get()
    }

    fun getPlayers(): LiveData<List<Player>?> = playersDao.getAll()

    fun getPlayer(id: Int): LiveData<Player?> = playersDao.get(id)

    private fun addPlayer(player: Player) {
        Executors.newSingleThreadExecutor().execute {
            playersDao.addPlayer(player)
        }
    }

    fun updatePlayer(player: Player) {
        Executors.newSingleThreadExecutor().execute {
            playersDao.update(player)
        }
    }
}