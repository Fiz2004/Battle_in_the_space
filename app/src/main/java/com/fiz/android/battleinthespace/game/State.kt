package com.fiz.android.battleinthespace.game

import android.media.SoundPool
import android.util.SparseIntArray
import com.fiz.android.battleinthespace.actor.PlayerGame
import java.io.Serializable

class State(
    var countPlayers: Int,
    var name: MutableList<String>,
    controllers: Array<Controller>,
    val soundMap: SparseIntArray,
    val soundPool: SoundPool,
) : Serializable {
    lateinit var level: Level
    var round: Int = 1
    var status: String = "playing"
    var playerGames: MutableList<PlayerGame> =
        MutableList(countPlayers) { index -> PlayerGame(index, controllers[index]) }

    init {
        newGame()
    }

    fun newGame() {
        round = 0
        status = "playing"
        newRound()

        for (player in playerGames)
            player.newGame()
        playerGames[0].main = true
    }

    private fun newRound() {
        round += 1

        level = Level(20.0, 20.0, countPlayers, round, playerGames)
    }

    fun update(controller: Array<Controller>, deltaTime: Double): Boolean {
        val levelStatus = level.update(controller, deltaTime, soundMap, soundPool)

        if (!levelStatus)
            newRound()

        if (round == 11)
            return false

        return true
    }

    fun clickPause() {
        status = if (status == "playing")
            "pause"
        else
            "playing"
    }
}
